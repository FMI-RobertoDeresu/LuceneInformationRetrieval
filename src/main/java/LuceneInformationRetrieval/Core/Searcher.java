package LuceneInformationRetrieval.Core;

import LuceneInformationRetrieval.Core.Models.FileModel;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class Searcher {
    private Analyzer _analyzer;
    private Directory _index;
    private int _docsPerPage;

    Searcher(Analyzer analyzer, Directory index, int docsPerPage) {
        _analyzer = analyzer;
        _index = index;
        _docsPerPage = docsPerPage;
    }

    public FileModel[] search(String queryStr) throws IOException, InvalidTokenOffsetsException, ParseException {
        Query query = new QueryParser("content", _analyzer).parse(queryStr);
        if (query == null)
            return new FileModel[]{};

        IndexReader indexReader = DirectoryReader.open(_index);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs docs = indexSearcher.search(query, _docsPerPage);

        Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">","</span>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 50);
        highlighter.setTextFragmenter(fragmenter);

        FileModel[] files = new FileModel[docs.scoreDocs.length];
        for (int i = 0; i < docs.scoreDocs.length; ++i) {
            int docId = docs.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docId);
            String name = doc.get("name");
            String text = doc.get("content");

            TokenStream stream = TokenSources.getTokenStream(indexReader, docId, "content", _analyzer);
            String[] frags = highlighter.getBestFragments(stream, text, 5);
            text = String.join("... ", frags);

            files[i] = new FileModel(name, text);
        }

        indexReader.close();

        return files;
    }
}
