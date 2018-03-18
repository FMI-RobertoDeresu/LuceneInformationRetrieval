import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Scanner;

import Models.File;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.commons.io.FileUtils;

public class Main {
    private final static String indexPathStr = "./files/index";
    private final static String docsPathStr = "./files/docs";
    private final static boolean buildIndex = true;
    private final static int docsPerPage = 10;

    public static void main(String[] args) throws IOException, NullPointerException {
        Analyzer analyzer = new Analyzer();

        // build or open index
        Directory index;
        if (buildIndex) {
            FileUtils.cleanDirectory(FileUtils.getFile(indexPathStr));
            index = new SimpleFSDirectory(Paths.get(indexPathStr));
            Indexer indexer = new Indexer(index, analyzer);

            java.io.File[] files = FileUtils.getFile(docsPathStr).listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    String name = file.getName();
                    String content = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
                    indexer.addDocument(new File(name, content));
                }
            }
            else {
                System.out.println("No files to index.");
            }
        }
        else {
            index = new SimpleFSDirectory(Paths.get(indexPathStr));
        }

        // query
        Scanner scanner = new Scanner(System.in);
        Searcher searcher = new Searcher(index, docsPerPage);

        while (true) {
            String queryStr = scanner.nextLine();
            Query query = new QueryBuilder(analyzer).createPhraseQuery("content", queryStr);
            File[] results = searcher.search(query);

            System.out.println("Found " + results.length + " hits.");
            for (int i = 0; i < results.length; ++i) {
                System.out.println((i + 1) + ". " + results[i].get_name());
            }
            System.out.println();
        }
    }
}