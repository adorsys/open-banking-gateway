package de.adorsys.opba.tppauthapi.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.tppauthapi.config.CookieProperties;
import de.adorsys.opba.tppauthapi.config.TppProperties;
import de.adorsys.opba.tppauthapi.model.generated.PsuAuthBody;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthApi;
import de.adorsys.opba.tppauthapi.service.PsuAuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_XSRF_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PsuAuthController implements PsuAuthApi {

    public static final Base64.Encoder ENCODER = Base64.getEncoder();
    private final PsuAuthService psuAuthService;
    private final CookieProperties cookieProperties;
    private final TppProperties tppProperties;

    @Override
    @SneakyThrows
    public ResponseEntity<String> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        Psu psu = psuAuthService.tryAuthenticateUser(psuAuthBody.getId(), psuAuthBody.getPassword());

        String jwtToken = generateToken(psu.getLogin());

        String sessionCookieString = ResponseCookie.from(AUTHORIZATION_SESSION_ID, jwtToken)
                .httpOnly(cookieProperties.isHttpOnly())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .path(cookieProperties.getPath())
                .maxAge(cookieProperties.getMaxAgeSeconds())
                .build().toString();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(X_REQUEST_ID, xRequestID.toString())
                .header(SET_COOKIE, sessionCookieString)
                .header(X_XSRF_TOKEN, ENCODER.encodeToString(jwtToken.getBytes()))
                .body(jwtToken);
    }

    @Override
    public ResponseEntity<Void> registration(PsuAuthBody psuAuthDto, UUID xRequestID) {
        psuAuthService.createPsuIfNotExist(psuAuthDto.getId(), psuAuthDto.getPassword());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.LOCATION, tppProperties.getLoginUrl());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @SneakyThrows
    public String generateToken(String id) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        Duration duration = Duration.ofSeconds(tppProperties.getKeyValidityDays());
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(Date.from(currentTime.plus(duration).toInstant()))
                .issueTime(Date.from(currentTime.toInstant()))
                .subject(String.valueOf(id))
                .build();
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.parse(tppProperties.getJwsAlgo())).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(new RSASSASigner(loadPrivateKey()));
        return signedJWT.serialize();
    }

    @SneakyThrows
    public PrivateKey loadPrivateKey() {
        byte[] privateKeyBytes = Base64.getDecoder().decode(tppProperties.getPrivateKey());
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppProperties.getSignAlgo());
        return kf.generatePrivate(ks);
    }

    @SneakyThrows
    public PublicKey loadPublicKey() {
        byte[] publicKeyBytes = Base64.getDecoder().decode(tppProperties.getPublicKey());
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppProperties.getSignAlgo());
        return kf.generatePublic(ks);
    }
}
