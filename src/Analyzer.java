import Filters.DiacriticsFilter;
import Filters.StemmerFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;

public class Analyzer extends org.apache.lucene.analysis.Analyzer {
    private int maxTokenLength;

    Analyzer() {
        maxTokenLength = 255;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer tokenizer = new StandardTokenizer();
        tokenizer.setMaxTokenLength(this.maxTokenLength);
        TokenStream tokenFilter = new StandardFilter(tokenizer);
        tokenFilter = new LowerCaseFilter(tokenFilter);
        tokenFilter = new DiacriticsFilter(tokenFilter);
        tokenFilter = new StemmerFilter(tokenFilter);

        return new TokenStreamComponents(tokenizer, tokenFilter) {
            protected void setReader(Reader reader) {
                tokenizer.setMaxTokenLength(maxTokenLength);
                super.setReader(reader);
            }
        };
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);

        return result;
    }
}
