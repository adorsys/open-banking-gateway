package de.adorsys.opba.protocol.xs2a.config.expirable;

import com.google.common.cache.CacheBuilder;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.InternalProcessResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.cache.CacheBuilder.newBuilder;

@Configuration
public class ExpirableDataConfig {

    private static final String PROTOCOL_CACHE_BUILDER = "protocol-cache-builder";

    public static final long MIN_EXPIRE_SECONDS = 60L;

    @Bean(PROTOCOL_CACHE_BUILDER)
    CacheBuilder protocolCacheBuilder(@Value("${protocol.expirable.expire-after-write}") Duration expireAfterWrite) {
        if (expireAfterWrite.getSeconds() < MIN_EXPIRE_SECONDS) {
            throw new IllegalArgumentException("It is not recommended to have short transient data expiration time, "
                    + "it must be at least equal to request timeout");
        }

        return newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .maximumSize(Integer.MAX_VALUE);
    }

    @Bean
    Map<String, Consumer<InternalProcessResult>> subscribers(@Qualifier(PROTOCOL_CACHE_BUILDER) CacheBuilder builder) {
        return builder.build().asMap();
    }

    @Bean
    Map<String, InternalProcessResult> deadLetterQueue(@Qualifier(PROTOCOL_CACHE_BUILDER) CacheBuilder builder) {
        return builder.build().asMap();
    }
}
