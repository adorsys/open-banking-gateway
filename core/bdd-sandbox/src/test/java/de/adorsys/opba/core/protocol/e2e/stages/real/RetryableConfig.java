package de.adorsys.opba.core.protocol.e2e.stages.real;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@Configuration
public class RetryableConfig {

    @Bean
    RetryOperations retryOperations(@Value("${test.retry.max}") int maxRetries) {
        RetryTemplate withRetry = new RetryTemplate();
        withRetry.setBackOffPolicy(new ExponentialBackOffPolicy());
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxRetries);
        withRetry.setRetryPolicy(retryPolicy);
        withRetry.registerListener(new LogRetryListener());
        return withRetry;
    }

    private static class LogRetryListener extends RetryListenerSupport {

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("Caught for retry {}", throwable.getMessage());
        }
    }
}
