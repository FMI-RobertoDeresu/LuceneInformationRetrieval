package LuceneInformationRetrieval.Core.Models;

public class TFIDFTermModel {
    private String term;
    private double tf;
    private double tfidf;

    public TFIDFTermModel(String term){
        this.term = term;
        this.tf = 0;
        this.tfidf = 0;
    }

    public String getTerm() {
        return term;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }
}
