package de.adorsys.opba.core.protocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "de.adorsys.opba.core.protocol.config",
                "de.adorsys.opba.core.protocol.controller",
                "de.adorsys.opba.core.protocol.service"
        }
)
public class BankingProtocol {

    public static void main(String[] args) {
        SpringApplication.run(BankingProtocol.class, args);
    }
}
