package de.adorsys.opba.fireflyexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class FireflyExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FireflyExporterApplication.class, args);
	}
}
