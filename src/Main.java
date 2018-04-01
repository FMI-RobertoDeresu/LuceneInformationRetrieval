import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import Models.FileModel;

import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class Main {
    private final static String indexPathStr = "./files/index";
    private final static String docsPathStr = "./files/docs";
    private final static String stopwordsPathStr = "./files/stopwords.txt";
    private final static boolean buildIndex = true;
    private final static int docsPerPage = 10;

    public static void main(String[] args) throws IOException, NullPointerException {
        java.io.File stopwordsFile = FileUtils.getFile(stopwordsPathStr);
        List<String> stopwords = FileUtils.readLines(stopwordsFile, Charset.forName("UTF-8"));

        Analyzer analyzer = new Analyzer(stopwords);

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
                    String path = file.getPath();
                    String[] ar = name.toLowerCase().split("\\.");
                    String extension = ar[ar.length - 1];
                    String text;

                    if (extension.endsWith("txt"))
                        text = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
                    else if (extension.endsWith("pdf")) {
                        File pdfFile = new File(path);
                        PDDocument pddDocument = PDDocument.load(pdfFile);
                        text = new PDFTextStripper().getText(pddDocument);
                    }
                    else if (extension.endsWith("docx") || extension.endsWith("doc")) {
                        FileInputStream inputStream = new FileInputStream(path);
                        XWPFDocument docx = new XWPFDocument(inputStream);
                        XWPFWordExtractor we = new XWPFWordExtractor(docx);
                        text = we.getText();
                    }
                    else {
                        throw new UnsupportedOperationException(String.format("File type not supported '%s'", name));
                    }

                    indexer.addDocument(new FileModel(name, text));
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
        System.out.println("Ready for queries!");

        Scanner scanner = new Scanner(System.in);
        Searcher searcher = new Searcher(index, docsPerPage);

        while (true) {
            String queryStr = scanner.nextLine();
            Query query = new QueryBuilder(analyzer).createPhraseQuery("content", queryStr);
            FileModel[] results = searcher.search(query);

            System.out.println("Found " + results.length + " hits.");
            for (int i = 0; i < results.length; ++i) {
                System.out.println((i + 1) + ". " + results[i].get_name());
            }
            System.out.println();
        }
    }
}