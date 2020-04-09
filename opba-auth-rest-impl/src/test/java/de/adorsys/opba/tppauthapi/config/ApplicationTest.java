package de.adorsys.opba.tppauthapi.config;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableConfigurationProperties
@EnableBankingPersistence
@Configuration
public class ApplicationTest {
}
