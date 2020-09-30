package de.adorsys.opba.fireflyexporter.client;

import de.adorsys.opba.fireflyexporter.service.FireFlyTokenProvider;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class FireFlyClientConfig {

    @Bean
    public RequestInterceptor requestInterceptorWithTokenAdding(FireFlyTokenProvider tokenProvider) {
        return requestTemplate -> requestTemplate.header(AUTHORIZATION, "Bearer " + tokenProvider.getToken());
    }
}
