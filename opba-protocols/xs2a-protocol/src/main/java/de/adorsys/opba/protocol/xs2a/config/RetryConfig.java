package de.adorsys.opba.protocol.xs2a.config;

import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Retry template provider to handle certain transient errors. Note that Flowable has its own retry strategy and logic.
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryOperations retryOperations(FlowableProperties flowableProperties) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(flowableProperties.getNumberOfRetries()));
        return retryTemplate;
    }
}
