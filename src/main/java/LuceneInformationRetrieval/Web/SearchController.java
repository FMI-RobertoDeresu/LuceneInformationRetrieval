package LuceneInformationRetrieval.Web;

import LuceneInformationRetrieval.Core.Builder;
import LuceneInformationRetrieval.Core.Models.FileModel;
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
    public String hello(@RequestParam(name = "query", required = false, defaultValue = "") String query, Model model)
            throws IOException, TikaException, InvalidTokenOffsetsException, ParseException {

        Builder builder = new Builder();
        Searcher searcher = builder.createSearcher(false);

        FileModel[] results = searcher.search(query);

        model.addAttribute("query", query);
        model.addAttribute("results", results);

        return "search";
    }

}
