package de.adorsys.opba.protocol.facade.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {
    private static final String ALGORITHM_RSA = "RSA";
    private static final String CLAIM_NAME = "sign-data";

    @Override
    public String verify(String signature, String encodedPublicKey) {

        try {
            PKCS8EncodedKeySpec encodedPublicKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(encodedPublicKeySpec);

            JwtParser parser = Jwts.parserBuilder()
                                       .setSigningKey(publicKey).build();

            Claims claims = (Claims) parser.parse(signature).getBody();
            return (String) claims.get(CLAIM_NAME);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("Signature verification error:  {}", signature);
            return null;
        }
    }
}
