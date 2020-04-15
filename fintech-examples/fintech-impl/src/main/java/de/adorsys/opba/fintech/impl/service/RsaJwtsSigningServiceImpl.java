package de.adorsys.opba.fintech.impl.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Data
@Service
@ConfigurationProperties("security")
public class RsaJwtsSigningServiceImpl implements RequestSigningService {
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.RS256;
    private static final String ALGORITHM_RSA = "RSA";
    private static final String CLAIM_NAME = "sign-data";

    private String privateKey;
    private String signIssuer;
    private String signSubject;

    @Override
    public String sign(String data) {
        try {
            PKCS8EncodedKeySpec encodedPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFact = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey generatedPrivateKey = keyFact.generatePrivate(encodedPrivateKeySpec);

            return generateSignature(generatedPrivateKey, data);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn("Encoded private key has wrong format :  {}", privateKey);
            return null;
        }
    }

    private String generateSignature(Key privateKey, String signData) {
        return Jwts.builder()
                       .setSubject(signSubject)
                       .setIssuer(signIssuer)
                       .claim(CLAIM_NAME, signData)
                       .signWith(privateKey, ALGORITHM)
                       .compact();
    }
}
