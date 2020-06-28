package de.adorsys.opba.protocol.sandbox.hbci.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HbciSandboxMapperConfig {

    @Bean
    ObjectMapper mapper() {
        return new ObjectMapper();
    }
}
