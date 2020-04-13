package de.adorsys.opba.protocol.facade.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RsaVerifyingServiceImpl implements RequestVerifyingService {
    private static final String CLAIM_NAME = "sign-data";

    @Override
    public String verify(String signature, String publicKey) {
        String encodedPublicKey = getNormalizeEncodedPublicKey(publicKey);

        /*try {*/
        Claims claims = Jwts.parser()
                                .setSigningKey(encodedPublicKey)
                                .parseClaimsJws(signature)
                                .getBody();
        return (String) claims.get(CLAIM_NAME);
        /*} catch (InvalidKeyException e) {
            log.warn("Error loading privet key form property!");
        }

        return null;*/
    }

    private String getNormalizeEncodedPublicKey(String publicKey) {

        return publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                       .replace("-----END PUBLIC KEY-----", "")
                       .replace("\n", "");
    }
}
