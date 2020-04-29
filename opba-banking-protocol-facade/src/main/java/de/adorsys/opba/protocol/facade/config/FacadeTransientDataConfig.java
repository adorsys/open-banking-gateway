package de.adorsys.opba.protocol.facade.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

@Configuration
public class FacadeTransientDataConfig {

    public static final long MIN_EXPIRE_SECONDS = 60L;
    public static final String FACADE_CACHE_BUILDER = "facade-cache-builder";

    @Bean(FACADE_CACHE_BUILDER)
    CacheBuilder facadeCacheBuilder(@Value("${" + FACADE_CONFIG_PREFIX + ".expirable.expire-after-write}") Duration expireAfterWrite) {
        if (expireAfterWrite.getSeconds() < MIN_EXPIRE_SECONDS) {
            throw new IllegalArgumentException("It is not recommended to have short transient data expiration time, "
                    + "it must be at least equal to request timeout");
        }

        return newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .maximumSize(Integer.MAX_VALUE);
    }
}
