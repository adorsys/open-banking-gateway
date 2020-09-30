package de.adorsys.opba.fireflyexporter.client;

import de.adorsys.opba.fireflyexporter.service.FireFlyTokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class FireFlyClientConfig {

    @Bean
    public RequestInterceptor requestInterceptorWithTokenAdding(FireFlyTokenProvider tokenProvider) {
        return requestTemplate -> requestTemplate.header(AUTHORIZATION, "Bearer " + tokenProvider.getToken());
    }
}
