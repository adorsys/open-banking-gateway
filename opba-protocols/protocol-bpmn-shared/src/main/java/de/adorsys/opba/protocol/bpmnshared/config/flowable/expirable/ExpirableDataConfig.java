package de.adorsys.opba.protocol.bpmnshared.config.flowable.expirable;

import com.google.common.cache.CacheBuilder;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalProcessResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.cache.CacheBuilder.newBuilder;

/**
 * This class manages transient data that expires after some time. For example if some process result was not
 * handled by {@link  de.adorsys.opba.protocol.bpmnshared.outcome.OutcomeMapper} within some time window
 * it will expire and will be removed from memory.
 */
@Configuration
public class ExpirableDataConfig {

    private static final String PROTOCOL_CACHE_BUILDER = "protocol-cache-builder";

    /**
     * To avoid wrong configuration it is meaningful to require that expirable data will be alive for at least this
     * amount of time.
     */
    public static final long MIN_EXPIRE_SECONDS = 60L;

    /**
     * @param flowableProperties contains 'expire-after-write' property - Duration for which the record will be alive
     *                           and it will be removed when this time frame passes.
     * @return Builder to build expirable maps.
     */
    @Bean(PROTOCOL_CACHE_BUILDER)
    CacheBuilder protocolCacheBuilder(FlowableProperties flowableProperties) {
        Duration expireAfterWrite = flowableProperties.getExpirable().getExpireAfterWrite();
        if (expireAfterWrite.getSeconds() < MIN_EXPIRE_SECONDS) {
            throw new IllegalArgumentException("It is not recommended to have short transient data expiration time, "
                    + "it must be at least equal to request timeout");
        }

        return newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .maximumSize(Integer.MAX_VALUE);
    }

    /**
     * Expirable subscribers to the process results. They will be alive for some time and then if no message
     * comes in - will be removed.
     */
    @Bean
    Map<String, Consumer<InternalProcessResult>> subscribers(@Qualifier(PROTOCOL_CACHE_BUILDER) CacheBuilder builder) {
        return builder.build().asMap();
    }

    /**
     * Expirable process results. They will be alive for some time and then if no handler consumes it - will be removed.
     */
    @Bean
    Map<String, InternalProcessResult> deadLetterQueue(@Qualifier(PROTOCOL_CACHE_BUILDER) CacheBuilder builder) {
        return builder.build().asMap();
    }
}
