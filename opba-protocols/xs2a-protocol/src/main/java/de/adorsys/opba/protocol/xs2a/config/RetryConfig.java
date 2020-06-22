package de.adorsys.opba.protocol.xs2a.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Retry template provider to handle certain transient errors. Note that Flowable has its own retry strategy and logic.
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryOperations retryOperations() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new NeverRetryPolicy()); // TODO: re-enable retry after proper error handling implementation
        return retryTemplate;
    }
}
