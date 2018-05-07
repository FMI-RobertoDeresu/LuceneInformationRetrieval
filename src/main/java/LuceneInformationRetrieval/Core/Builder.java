package LuceneInformationRetrieval.Core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import LuceneInformationRetrieval.Core.Models.FileModel;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.commons.io.FileUtils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class Builder {
    private final static String indexPathStr = "./files/index";
    private final static String docsPathStr = "./files/docs";
    private final static String stopwordsPathStr = "./files/stopwords.txt";
    private final static int docsPerPage = 10;

    public Searcher createSearcher(boolean buildIndex) throws IOException, NullPointerException, TikaException {
        File stopwordsFile = FileUtils.getFile(stopwordsPathStr);
        List<String> stopwords = FileUtils.readLines(stopwordsFile, Charset.forName("UTF-8"));

        Analyzer analyzer = new Analyzer(stopwords);

        // create index directory if not exists
        File indexDirectory = new File(indexPathStr);
        if (!indexDirectory.exists())
            indexDirectory.mkdir();

        // build or open index
        Directory index;
        if (buildIndex) {
            FileUtils.cleanDirectory(FileUtils.getFile(indexPathStr));
            index = new SimpleFSDirectory(Paths.get(indexPathStr));
            Indexer indexer = new Indexer(index, analyzer);

            Tika tika = new Tika();
            File[] files = FileUtils.getFile(docsPathStr).listFiles();

            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String text = tika.parseToString(file.toPath());
                    indexer.addDocument(new FileModel(name, text));
                }
            } else {
                System.out.println("No files to index.");
            }
        } else {
            index = new SimpleFSDirectory(Paths.get(indexPathStr));
        }

        Searcher searcher = new Searcher(analyzer, index, docsPerPage);

        return searcher;
    }
}