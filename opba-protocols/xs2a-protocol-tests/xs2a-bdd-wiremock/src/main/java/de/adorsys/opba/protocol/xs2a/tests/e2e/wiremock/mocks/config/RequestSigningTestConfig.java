package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.config;

import de.adorsys.opba.api.security.EnableSignRequestBasedApiSecurity;
import de.adorsys.opba.api.security.service.RequestSigningService;
import de.adorsys.opba.api.security.service.impl.RsaJwtsSigningServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Data
@Slf4j
@EnableSignRequestBasedApiSecurity
@ConfigurationProperties("security")
@Validated
public class RequestSigningTestConfig {
    @NotBlank
    private String encodedPrivateKey;
    @NotBlank
    private String signIssuer;
    @NotBlank
    private String signSubject;
    @NotBlank
    private String algorithm;
    @NotBlank
    private String claimNameKey;

    @Bean
    public RequestSigningService requestSigningService() {
        return new RsaJwtsSigningServiceImpl(parsePrivateKey(), signIssuer, signSubject, claimNameKey);
    }

    private PrivateKey parsePrivateKey() {
        try {
            PKCS8EncodedKeySpec encodedPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedPrivateKey));
            KeyFactory keyFact = KeyFactory.getInstance(algorithm);
            return keyFact.generatePrivate(encodedPrivateKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            String message = String.format("Encoded private key has wrong format :  %s ", e);
            log.error(message);
            throw new IllegalArgumentException(e);
        }
    }
}
