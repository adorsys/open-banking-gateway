package de.adorsys.opba.tppbankingapi;

import de.adorsys.opba.db.config.EnableMigration;
import de.adorsys.opba.tppbankingapi.config.EnableBankingPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EnableMigration
@EnableBankingPersistence
public class ApplicationTest {
}
