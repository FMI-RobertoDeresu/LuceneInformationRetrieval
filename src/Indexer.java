import Models.File;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class Indexer {
    private Directory _index;
    private org.apache.lucene.analysis.Analyzer _analyzer;

    Indexer(Directory index, org.apache.lucene.analysis.Analyzer analyzer) {
        _index = index;
        _analyzer = analyzer;
    }

    void addDocument(File file) throws IOException {
        File[] docs = new File[] {file};
        addDocuments(docs);
    }

    private void addDocuments(File[] files) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);
        IndexWriter indexWriter = new IndexWriter(_index, config);

        for (File file : files){
            Document document = new Document();
            document.add(new TextField("name", file.get_name(), Field.Store.YES));
            document.add(new TextField("content", file.get_content(), Field.Store.YES));
            indexWriter.addDocument(document);
        }

        indexWriter.close();
    }
}
