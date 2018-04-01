import Models.FileModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class Searcher {
    private Directory _index;
    private int _docsPerPage;

    Searcher(Directory index, int docsPerPage) {
        _index = index;
        _docsPerPage = docsPerPage;
    }

    public FileModel[] search(Query query) throws IOException {
        if (query == null)
            return new FileModel[]{};

        IndexReader indexReader = DirectoryReader.open(_index);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs docs = indexSearcher.search(query, _docsPerPage);

        FileModel[] files = new FileModel[docs.scoreDocs.length];
        for (int i = 0; i < docs.scoreDocs.length; ++i) {
            int docId = docs.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docId);
            files[i] = new FileModel(doc.get("name"), doc.get("content"));
        }

        indexReader.close();
        return files;
    }
}
