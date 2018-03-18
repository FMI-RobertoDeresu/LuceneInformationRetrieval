package Models;

public class File {
    private String _name;
    private String _content;

    public File(String name, String content) {
        _name = name;
        _content = content;
    }

    public String get_name() {
        return _name;
    }

    public String get_content() {
        return _content;
    }
}
