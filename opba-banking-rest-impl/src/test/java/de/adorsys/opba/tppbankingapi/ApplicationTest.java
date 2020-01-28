package de.adorsys.opba.tppbankingapi;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EnableBankingPersistence
public class ApplicationTest {
}
