package de.adorsys.opba.api.security.service.impl;

import de.adorsys.opba.api.security.domain.SignData;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
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

import static de.adorsys.opba.api.security.service.SignatureParams.ALGORITHM_RSA;
import static de.adorsys.opba.api.security.service.SignatureParams.CLAIM_NAME;

@Slf4j
@Service
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {

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
                           .equals(claims.get(CLAIM_NAME.getValue()));

        } catch (Exception e) {
            log.warn("Signature verification error:  {}", signature);
            return false;
        }
    }

    private PublicKey getRsaPublicKey(String encodedPublicKey) {
        try {
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA.getValue());

            return keyFactory.generatePublic(encodedPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("Encoded public key has wrong format :  {}", encodedPublicKey);
            return null;
        }
    }
}
