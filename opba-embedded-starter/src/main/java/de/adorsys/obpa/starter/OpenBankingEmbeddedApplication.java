package de.adorsys.obpa.starter;

import de.adorsys.opba.bankingapi.config.EnableBankingApi;
import de.adorsys.opba.bankingapi.config.swagger.EnableBankingApiSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBankingApi
@EnableBankingApiSwagger
public class OpenBankingEmbeddedApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenBankingEmbeddedApplication.class, args);
	}

}
