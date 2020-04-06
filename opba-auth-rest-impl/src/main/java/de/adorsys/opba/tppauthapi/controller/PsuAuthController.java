package de.adorsys.opba.tppauthapi.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.tppauthapi.model.generated.PsuAuthBody;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthApi;
import de.adorsys.opba.tppauthapi.service.PsuAuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PsuAuthController implements PsuAuthApi {
    private final PsuAuthService psuAuthService;

    @Override
    public ResponseEntity<String> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        return null;
    }

    @Override
    @SneakyThrows
    public ResponseEntity<Void> registration(PsuAuthBody psuAuthDto, UUID xRequestID) {
        Psu psu = psuAuthService.createPsuIfNotExist(psuAuthDto.getId(), psuAuthDto.getPassword());
        KeyPairGenerator generator = KeyPairGenerator.getInstance("AES");
        KeyPair keyPair = generator.generateKeyPair();
        generateToken(psu.getUserId(), keyPair.getPrivate(), Duration.ofDays(7));
        return null;
        // return jwttoken as httpOnly cookie,  xsrftoken in response body
    }

    @SneakyThrows
    private String generateToken(String id, PrivateKey privateKey, TemporalAmount validity) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(Date.from(currentTime.plus(validity).toInstant()))
                .issueTime(Date.from(currentTime.toInstant()))
                .subject(String.valueOf(id))
                .build();
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(new RSASSASigner(privateKey));
        return signedJWT.serialize();
    }
}
