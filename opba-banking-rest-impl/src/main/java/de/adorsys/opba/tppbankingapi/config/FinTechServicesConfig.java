package de.adorsys.opba.tppbankingapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("fintech-db.yml")
public class FinTechServicesConfig {

    private final Map<String, String> fintechServices = new HashMap<>();

}
