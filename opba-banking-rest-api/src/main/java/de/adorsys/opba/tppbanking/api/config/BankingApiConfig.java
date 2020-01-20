package de.adorsys.opba.tppbanking.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"de.adorsys.opba.tppbanking", "de.adorsys.opba.protocol.services"})
public class BankingApiConfig {
}
