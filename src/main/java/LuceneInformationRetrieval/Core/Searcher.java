package LuceneInformationRetrieval.Core;

import LuceneInformationRetrieval.Core.Models.*;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Searcher {
    private Analyzer _analyzer;
    private Directory _index;
    private int _docsPerPage;
    private boolean _highlight;

    Searcher(Analyzer analyzer, Directory index, int docsPerPage, boolean highlight) {
        _analyzer = analyzer;
        _index = index;
        _docsPerPage = docsPerPage;
        _highlight = highlight;
    }

    public FileModel[] search(String queryStr) throws IOException, InvalidTokenOffsetsException, ParseException {
        QueryParser queryParser = new QueryParser("content", _analyzer);
        Query query = queryParser.parse(queryStr);
        if (query == null)
            return new FileModel[]{};

        IndexReader indexReader = DirectoryReader.open(_index);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs docs = indexSearcher.search(query, _docsPerPage);

        Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 40);
        highlighter.setTextFragmenter(fragmenter);

        FileModel[] files = new FileModel[docs.scoreDocs.length];
        for (int i = 0; i < docs.scoreDocs.length; ++i) {
            int docId = docs.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docId);
            String name = doc.get("name");
            String text = doc.get("content");

            TokenStream stream = TokenSources.getTokenStream(indexReader, docId, "content", _analyzer);
            String[] frags = highlighter.getBestFragments(stream, text, 1);
            text = _highlight ? String.join("... ", frags) : text;

            files[i] = new FileModel(name, text);
        }

        indexReader.close();

        return files;
    }

    public TFIDFModel getTFIDF(String queryStr) throws ParseException, IOException {
        TFIDFModel model = new TFIDFModel();

        IndexReader indexReader = DirectoryReader.open(_index);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        model.setTermsIDFs(getTermsIDF(queryStr, indexReader, indexSearcher));
        model.setFilesTFs(getTF(queryStr, indexReader, indexSearcher));

        indexReader.close();

        return model;
    }

    private Map<String, Double> getTermsIDF(String queryStr, IndexReader indexReader, IndexSearcher indexSearcher) throws IOException, ParseException {
        Map<String, Double> termsIDF = new HashedMap<>();
        String[] termsStr = queryStr.split("\\s+");

        for (String queryTerm : termsStr) {
            double idf = getTermIDF(queryTerm, indexReader, indexSearcher);
            termsIDF.put(queryTerm, idf);
        }

        return termsIDF;
    }

    private double getTermIDF(String queryTermStr, IndexReader indexReader, IndexSearcher indexSearcher) throws IOException, ParseException {
        double idf = 0;

        QueryParser queryParser = new QueryParser("content", _analyzer);
        Query query = queryParser.parse(queryTermStr);
        if (query == null)
            return idf;

        Set<Term> terms = new HashSet<>();
        query.createWeight(indexSearcher, false).extractTerms(terms);

        if (terms.isEmpty())
            return idf;

        Term term = terms.iterator().next();
        long N = indexReader.numDocs();
        double docFreq = indexReader.docFreq(term);
        idf = (docFreq != 0) ? Math.log10(N / docFreq) : 0;

        return idf;
    }

    private List<TFIDFFileModel> getTF(String queryStr, IndexReader indexReader, IndexSearcher indexSearcher) throws IOException, ParseException {
        List<TFIDFFileModel> filesTermsTfs = new LinkedList<>();
        List<Integer> docsIds = getDocs(indexReader);

        for (int docId : docsIds) {
            Document doc = indexReader.document(docId);
            List<TFIDFTermModel> termsTF = getDocTF(docId, queryStr, indexReader, indexSearcher);
            TFIDFFileModel fileTF = new TFIDFFileModel(doc.get("name"), termsTF);
            filesTermsTfs.add(fileTF);
        }

        return filesTermsTfs;
    }

    private List<TFIDFTermModel> getDocTF(int docId, String queryStr, IndexReader indexReader, IndexSearcher indexSearcher) throws IOException, ParseException {
        String[] queryTerms = queryStr.split("\\s+");
        List<TFIDFTermModel> termsTfs = new LinkedList<>();

        for (String queryTerm : queryTerms) {
            TFIDFTermModel termTF = getTermTF(docId, queryTerm, indexReader, indexSearcher);
            termsTfs.add(termTF);
        }

        return termsTfs;
    }

    private TFIDFTermModel getTermTF(int docId, String queryTermStr, IndexReader indexReader, IndexSearcher indexSearcher) throws IOException, ParseException {
        TFIDFTermModel model = new TFIDFTermModel(queryTermStr);

        QueryParser queryParser = new QueryParser("content", _analyzer);
        Query query = queryParser.parse(queryTermStr);
        String queryTerm = query.toString().substring(8);

        if (query == null)
            return model;

        Terms termsVector = indexReader.getTermVector(docId, "content2");
        TermsEnum termsEnum = termsVector.iterator();

        BytesRef term;
        while ((term = termsEnum.next()) != null) {
            String termStr = term.utf8ToString();
            PostingsEnum postingsEnum = termsEnum.postings(null);
            while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                if (termStr.equals(queryTerm)) {
                    double freq = postingsEnum.freq();
                    double tf = freq > 0 ? (1 + Math.log10(freq)) : freq;
                    double tfidf = getTermIDF(queryTermStr, indexReader, indexSearcher) * tf;

                    model.setTf(tf);
                    model.setTfidf(tfidf);
                }
            }
        }

        return model;
    }

    private List<Integer> getDocs(IndexReader indexReader) throws IOException {
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new MatchAllDocsQuery();
        TopDocs td = indexSearcher.search(query, indexReader.numDocs());
        List<Integer> docsIds = Arrays.stream(td.scoreDocs).map(d -> d.doc).collect(Collectors.toList());

        return docsIds;
    }
}