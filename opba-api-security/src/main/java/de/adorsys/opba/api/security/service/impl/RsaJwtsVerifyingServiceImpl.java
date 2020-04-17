package de.adorsys.opba.api.security.service.impl;

import de.adorsys.opba.api.security.domain.SignData;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
import de.adorsys.opba.api.security.service.SignatureParams;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {
    private static final String ALGORITHM = SignatureParams.ALGORITHM_RSA.getValue();

    @Override
    public boolean verify(String signature, String encodedPublicKey, SignData signData) {
        PublicKey publicKey = getRsaPublicKey(encodedPublicKey);

        if (publicKey == null) {
            return false;
        }

        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            Claims claims = parser.parseClaimsJws(signature).getBody();

            return signData.convertDataToString()
                           .equals(claims.get(SignatureParams.CLAIM_NAME.getValue()));

        } catch (Exception e) {
            log.warn("Signature verification error:  {}", signature);
            return false;
        }
    }

    private PublicKey getRsaPublicKey(String encodedPublicKey) {
        try {
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            return keyFactory.generatePublic(encodedPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("Encoded public key has wrong format :  {}", encodedPublicKey);
            return null;
        }
    }
}
