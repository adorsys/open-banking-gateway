package de.adorsys.opba.protocol.facade.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {
    private static final String ALGORITHM_RSA = "RSA";
    private static final String CLAIM_NAME = "sign-data";

    @Override
    public String verify(String signature, String encodedPublicKey) {
        PublicKey publicKey = getRsaPublicKey(encodedPublicKey);

        if (publicKey == null) {
            return null;
        }

        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            Claims claims = parser.parseClaimsJws(signature).getBody();

            return (String) claims.get(CLAIM_NAME);
        } catch (Exception e) {
            log.warn("Signature verification error:  {}", signature);
            return null;
        }
    }

    private PublicKey getRsaPublicKey(String encodedPublicKey) {
        try {
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

            return keyFactory.generatePublic(encodedPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("Encoded public key has wrong format :  {}", encodedPublicKey);
            return null;
        }
    }
}
