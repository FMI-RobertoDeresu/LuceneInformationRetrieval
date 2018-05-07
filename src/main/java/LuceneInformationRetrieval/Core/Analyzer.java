package LuceneInformationRetrieval.Core;

import LuceneInformationRetrieval.Core.Filters.StemmerFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;
import java.util.List;

public class Analyzer extends org.apache.lucene.analysis.Analyzer {
    private int maxTokenLength;
    private CharArraySet _stopwords;

    Analyzer(List<String> stopwords) {
        _stopwords = new CharArraySet(stopwords, false);
        maxTokenLength = 255;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer tokenizer = new StandardTokenizer();
        tokenizer.setMaxTokenLength(this.maxTokenLength);
        TokenStream tokenStream = new StandardFilter(tokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StemmerFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, _stopwords);
        tokenStream = new ASCIIFoldingFilter(tokenStream);

        return new TokenStreamComponents(tokenizer, tokenStream) {
            protected void setReader(Reader reader) {
                tokenizer.setMaxTokenLength(maxTokenLength);
                super.setReader(reader);
            }
        };
    }
}
