package LuceneInformationRetrieval.Web;

import LuceneInformationRetrieval.Core.Builder;
import org.apache.tika.exception.TikaException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class LuceneInformationRetrievalApplication {

	public static void main(String[] args) throws IOException, TikaException {
		SpringApplication.run(LuceneInformationRetrievalApplication.class, args);

		// build lucene index
        new Builder().createSearcher(true);
	}
}
