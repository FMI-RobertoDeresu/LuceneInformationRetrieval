import Models.FileModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;

public class Indexer {
    private Directory _index;
    private org.apache.lucene.analysis.Analyzer _analyzer;

    Indexer(Directory index, org.apache.lucene.analysis.Analyzer analyzer) {
        _index = index;
        _analyzer = analyzer;
    }

    void addDocument(FileModel file) throws IOException {
        FileModel[] docs = new FileModel[] {file};
        addDocuments(docs);
    }

    private void addDocuments(FileModel[] files) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);
        IndexWriter indexWriter = new IndexWriter(_index, config);

        for (FileModel file : files){
            Document document = new Document();
            document.add(new TextField("name", file.get_name(), Field.Store.YES));
            document.add(new TextField("content", file.get_text() , Field.Store.YES));
            indexWriter.addDocument(document);
        }

        indexWriter.close();
    }
}
