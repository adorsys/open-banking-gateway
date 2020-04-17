//package de.adorsys.opba.api.security.config;
//
//import de.adorsys.opba.api.security.EnableVerifySignatureBasedApiSecurity;
//import de.adorsys.opba.api.security.filter.RequestSignatureValidationFilter;
//import de.adorsys.opba.api.security.service.RequestVerifyingService;
//import de.adorsys.opba.api.security.service.impl.RsaJwtsVerifyingServiceImpl;
//import lombok.Data;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//
//import java.time.Duration;
//
//@Data
//@EnableVerifySignatureBasedApiSecurity
//public class VerifyingConfig {
//    private Duration requestTimeLimit;
//
//    @Bean
//    public RequestSignatureValidationFilter requestSignatureValidationFilter(Environment environment) {
//        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl();
//        return new RequestSignatureValidationFilter(requestVerifyingService, requestTimeLimit, environment);
//    }
//}
