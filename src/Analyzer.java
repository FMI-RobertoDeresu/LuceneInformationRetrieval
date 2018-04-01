import Filters.DiacriticsFilter;
import Filters.StemmerFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
        TokenStream tokenFilter = new StandardFilter(tokenizer);
        tokenFilter = new LowerCaseFilter(tokenFilter);
        tokenFilter = new DiacriticsFilter(tokenFilter);
        tokenFilter = new StemmerFilter(tokenFilter);
        tokenFilter = new StopFilter(tokenFilter, _stopwords);

        return new TokenStreamComponents(tokenizer, tokenFilter) {
            protected void setReader(Reader reader) {
                tokenizer.setMaxTokenLength(maxTokenLength);
                super.setReader(reader);
            }
        };
    }
}
