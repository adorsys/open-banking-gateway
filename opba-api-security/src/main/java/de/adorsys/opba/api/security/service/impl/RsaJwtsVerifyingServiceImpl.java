package de.adorsys.opba.api.security.service.impl;

import de.adorsys.opba.api.security.domain.DataToSign;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {
    private final String claimNameKey;

    @Override
    public boolean verify(String signature, String encodedPublicKey, DataToSign dataToSign) {
        PublicKey publicKey = getRsaPublicKey(encodedPublicKey);

        if (publicKey == null) {
            return false;
        }

        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            Claims claims = parser.parseClaimsJws(signature).getBody();

            return dataToSign.convertDataToString()
                           .equals(claims.get(claimNameKey));

        } catch (Exception e) {
            log.error("Signature verification error:  {} for signature {}", e.getMessage(), signature);
            return false;
        }
    }

    private PublicKey getRsaPublicKey(String encodedPublicKey) {
        try {
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(encodedPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Encoded public key has wrong format :  {}", encodedPublicKey);
            return null;
        }
    }
}
