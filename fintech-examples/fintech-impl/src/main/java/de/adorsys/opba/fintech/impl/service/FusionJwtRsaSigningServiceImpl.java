package de.adorsys.opba.fintech.impl.service;


import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSASigner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Data
@Service
@ConfigurationProperties("security")
public class FusionJwtRsaSigningServiceImpl implements RequestSigningService {
    private static final String CLAIM_NAME = "sign-data";

    private String privateKeyPath;
    private String signIssuer;
    private String signSubject;

    @Override
    public String sign(String data) {
        String privateKey = readPemFileAsString();

        if (privateKey == null) {
            return null;
        }

        Signer signer = RSASigner.newSHA256Signer(privateKey);
        JWT jwt = new JWT().setIssuer(signIssuer)
                          .setSubject(signSubject)
                          .addClaim(CLAIM_NAME, data);

        return JWT.getEncoder().encode(jwt, signer);
    }

    private String readPemFileAsString() {
        try {
            return IOUtils.toString(Objects.requireNonNull(getClass()
                                                                   .getClassLoader()
                                                                   .getResourceAsStream(privateKeyPath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Error reading pem file");
            return null;
        }
    }
}
