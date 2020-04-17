//package de.adorsys.opba.api.security.config;
//
//import de.adorsys.opba.api.security.EnableSignRequestBasedApiSecurity;
//import de.adorsys.opba.api.security.service.RequestSigningService;
//import de.adorsys.opba.api.security.service.impl.RsaJwtsSigningServiceImpl;
//import lombok.Data;
//import org.springframework.context.annotation.Bean;
//
//@Data
//@EnableSignRequestBasedApiSecurity
//public class SigningConfig {
//    private String privateKey;
//    private String signIssuer;
//    private String signSubject;
//
//    @Bean
//    public RequestSigningService requestSigningService() {
//        return new RsaJwtsSigningServiceImpl(privateKey, signIssuer, signSubject);
//    }
//}
