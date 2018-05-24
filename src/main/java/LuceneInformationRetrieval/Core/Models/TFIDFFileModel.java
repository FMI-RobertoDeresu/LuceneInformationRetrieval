package LuceneInformationRetrieval.Core.Models;

import java.util.List;

public class TFIDFFileModel {
    private String fileName;
    private List<TFIDFTermModel> termsTFs;

    public TFIDFFileModel(String fileName, List<TFIDFTermModel> terms){
        this.fileName = fileName;
        this.termsTFs = terms;
    }

    public String getFileName() {
        return fileName;
    }

    public List<TFIDFTermModel> getTermsTFs() {
        return termsTFs;
    }
}
