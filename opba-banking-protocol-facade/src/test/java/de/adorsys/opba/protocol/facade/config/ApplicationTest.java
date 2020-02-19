package de.adorsys.opba.protocol.facade.config;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableXs2aProtocol
@EnableBankingPersistence
@SpringBootApplication(scanBasePackages = {
        "de.adorsys.opba.protocol.facade",
        "de.adorsys.opba.db"
})
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
