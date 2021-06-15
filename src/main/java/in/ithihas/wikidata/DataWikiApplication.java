package in.ithihas.wikidata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"in.ithihas.wikidata"})
public class DataWikiApplication {
	public static void main(String[] args) {
		SpringApplication.run(DataWikiApplication.class, args);
	}
}
