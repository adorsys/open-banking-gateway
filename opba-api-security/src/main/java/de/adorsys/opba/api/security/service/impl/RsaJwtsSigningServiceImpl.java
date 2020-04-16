package de.adorsys.opba.api.security.service.impl;


import de.adorsys.opba.api.security.domain.SignData;
import de.adorsys.opba.api.security.service.RequestSigningService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static de.adorsys.opba.api.security.service.SignatureParams.ALGORITHM_RSA;
import static de.adorsys.opba.api.security.service.SignatureParams.CLAIM_NAME;

@Slf4j
@RequiredArgsConstructor
public class RsaJwtsSigningServiceImpl implements RequestSigningService {
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.RS256;

    private final String privateKey;
    private final String signIssuer;
    private final String signSubject;

    @Override
    public String sign(SignData signData) {
        try {
            PKCS8EncodedKeySpec encodedPrivateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFact = KeyFactory.getInstance(ALGORITHM_RSA.getValue());
            PrivateKey generatedPrivateKey = keyFact.generatePrivate(encodedPrivateKeySpec);

            return generateSignature(generatedPrivateKey, signData.convertDataToString());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn("Encoded private key has wrong format :  {}", privateKey);
            return null;
        }
    }

    private String generateSignature(Key privateKey, String signData) {
        return Jwts.builder()
                       .setSubject(signSubject)
                       .setIssuer(signIssuer)
                       .claim(CLAIM_NAME.getValue(), signData)
                       .signWith(privateKey, ALGORITHM)
                       .compact();
    }
}
