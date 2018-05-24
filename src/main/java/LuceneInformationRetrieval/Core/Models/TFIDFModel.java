package LuceneInformationRetrieval.Core.Models;

import org.apache.commons.collections4.map.HashedMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TFIDFModel {
    private Map<String, Double> termsIDFs;
    private List<TFIDFFileModel> filesTFs;

    public TFIDFModel(){
        this.termsIDFs = new HashedMap<>();
        this.filesTFs = new LinkedList<>();
    }

    public Map<String, Double> getTermsIDFs() {
        return termsIDFs;
    }

    public void setTermsIDFs(Map<String, Double> termsIDFs) {
        this.termsIDFs = termsIDFs;
    }

    public List<TFIDFFileModel> getFilesTFs() {
        return filesTFs;
    }

    public void setFilesTFs(List<TFIDFFileModel> filesTFs) {
        this.filesTFs = filesTFs;
    }
}


