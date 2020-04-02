package de.adorsys.opba.protocol.xs2a.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean
    public RetryOperations retryOperations() {
        return new RetryTemplate();
    }
}
