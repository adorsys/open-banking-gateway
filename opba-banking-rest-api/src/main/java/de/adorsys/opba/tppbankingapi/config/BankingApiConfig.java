package de.adorsys.opba.tppbankingapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"de.adorsys.opba.tppbankingapi", "de.adorsys.opba.tppbanking.services.ais"})
public class BankingApiConfig {
}
