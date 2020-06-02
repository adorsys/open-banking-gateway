package de.adorsys.fintech.tests.e2e.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@EnableRetry
@Configuration
public class RetryableConfig {

    public static final String TEST_RETRY_OPS = "TEST_RETRY_OPS";

    @Bean(name = TEST_RETRY_OPS)
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
