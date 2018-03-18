package Filters;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.romanianStemmer;

import java.io.IOException;

public class StemmerFilter extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public StemmerFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            String current = termAtt.toString();
            SnowballStemmer stemmer = new romanianStemmer();
            stemmer.setCurrent(current);
            stemmer.stem();
            current = stemmer.getCurrent();
            termAtt.copyBuffer(current.toCharArray(), 0, current.toCharArray().length);

            return true;
        } else {
            return false;
        }
    }




}
