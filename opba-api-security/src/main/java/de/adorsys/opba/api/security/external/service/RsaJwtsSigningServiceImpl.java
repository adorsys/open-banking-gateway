package de.adorsys.opba.api.security.external.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;

@Slf4j
public class RsaJwtsSigningServiceImpl implements RequestSigningService {
    private final PrivateKey privateKey;
    private final String signIssuer;
    private final String signSubject;
    private final SignatureAlgorithm algorithm;
    private final String claimNameKey;

    public RsaJwtsSigningServiceImpl(PrivateKey privateKey, String signIssuer, String signSubject, String claimNameKey) {
        this.privateKey = privateKey;
        this.signIssuer = signIssuer;
        this.signSubject = signSubject;
        this.claimNameKey = claimNameKey;
        this.algorithm = SignatureAlgorithm.forSigningKey(privateKey);
    }

    @Override
    public String signature(String dataToSign) {
        return Jwts.builder()
                .setSubject(signSubject)
                .setIssuer(signIssuer)
                .claim(claimNameKey, dataToSign)
                .signWith(privateKey, algorithm)
                .compact();
    }
}
