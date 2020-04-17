package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.api.security.EnableSignRequestBasedApiSecurity;
import de.adorsys.opba.api.security.service.RequestSigningService;
import de.adorsys.opba.api.security.service.SignatureParams;
import de.adorsys.opba.api.security.service.impl.RsaJwtsSigningServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
public class RequestSigningConfig {
    private String encodedPrivateKey;
    private String signIssuer;
    private String signSubject;

    @Bean
    public RequestSigningService requestSigningService() {
        return new RsaJwtsSigningServiceImpl(parsePrivateKey(), signIssuer, signSubject);
    }

    private PrivateKey parsePrivateKey() {
        if (StringUtils.isBlank(encodedPrivateKey)) {
            throw new IllegalArgumentException("Encoded private key is empty");
        }

        try {
            PKCS8EncodedKeySpec encodedPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedPrivateKey));
            KeyFactory keyFact = KeyFactory.getInstance(SignatureParams.ALGORITHM_RSA.getValue());
            return keyFact.generatePrivate(encodedPrivateKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            String message = String.format("Encoded private key has wrong format :  %s ", encodedPrivateKey);
            log.warn(message);
            throw new IllegalArgumentException(message);
        }
    }
}
