package Filters;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class DiacriticsFilter extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public DiacriticsFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            for (int index = 0; index < termAtt.length(); index++){
                switch (termAtt.buffer()[index]){
                    case 'ă':
                        termAtt.buffer()[index] = 'a';
                        break;
                    case 'î':
                        termAtt.buffer()[index] = 'i';
                        break;
                    case 'â':
                        termAtt.buffer()[index] = 'a';
                        break;
                    case 'ș':
                        termAtt.buffer()[index] = 's';
                        break;
                    case 'ț':
                        termAtt.buffer()[index] = 't';
                        break;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
