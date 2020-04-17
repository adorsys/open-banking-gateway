package de.adorsys.opba.api.security.service.impl;

import de.adorsys.opba.api.security.domain.SignData;
import de.adorsys.opba.api.security.service.RequestSigningService;
import de.adorsys.opba.api.security.service.SignatureParams;
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

    public RsaJwtsSigningServiceImpl(PrivateKey privateKey, String signIssuer, String signSubject) {
        this.privateKey = privateKey;
        this.signIssuer = signIssuer;
        this.signSubject = signSubject;
        this.algorithm = SignatureAlgorithm.forSigningKey(privateKey);
    }

    public RsaJwtsSigningServiceImpl(PrivateKey privateKey, String signIssuer, String signSubject, SignatureAlgorithm signatureAlgorithm) {
        this.privateKey = privateKey;
        this.signIssuer = signIssuer;
        this.signSubject = signSubject;
        this.algorithm = signatureAlgorithm;
    }

    @Override
    public String sign(SignData signData) {
        return Jwts.builder()
                       .setSubject(signSubject)
                       .setIssuer(signIssuer)
                       .claim(SignatureParams.CLAIM_NAME.getValue(), signData.convertDataToString())
                       .signWith(privateKey, algorithm)
                       .compact();
    }
}
