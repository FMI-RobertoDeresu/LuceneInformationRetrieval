package LuceneInformationRetrieval.Web;

import LuceneInformationRetrieval.Core.Builder;
import LuceneInformationRetrieval.Core.Models.*;
import LuceneInformationRetrieval.Core.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "showtfidf", required = false, defaultValue = "false") String showtfidf,
            @RequestParam(name = "showresults", required = false, defaultValue = "false") String showresults,
            Model model)
            throws IOException, TikaException, InvalidTokenOffsetsException, ParseException {
        Builder builder = new Builder();
        Searcher searcher = builder.createSearcher(false, false);

        FileModel[] results = searcher.search(query);
        TFIDFModel tfidf = searcher.getTFIDF(query);

        model.addAttribute("query", query);
        model.addAttribute("showtfidf", showtfidf);
        model.addAttribute("showresults", showresults);
        model.addAttribute("results", results);
        model.addAttribute("tfidf", tfidf);

        return "search";
    }
}
