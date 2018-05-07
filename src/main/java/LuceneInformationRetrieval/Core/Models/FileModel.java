package LuceneInformationRetrieval.Core.Models;

public class FileModel {
    private String _name;
    private String _text;

    public FileModel(String name, String text) {
        _name = name;
        _text = text;
    }

    public String get_name() {
        return _name;
    }

    public String get_text() {
        return _text;
    }
}
