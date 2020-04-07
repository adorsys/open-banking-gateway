package de.adorsys.opba.tppauthapi.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.tppauthapi.config.CookieProperties;
import de.adorsys.opba.tppauthapi.model.generated.PsuAuthBody;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthApi;
import de.adorsys.opba.tppauthapi.service.PsuAuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_XSRF_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PsuAuthController implements PsuAuthApi {
    public static final Duration WEEK = Duration.ofDays(7);

    private final PsuAuthService psuAuthService;
    private final CookieProperties cookieProperties;

    @Override
    @SneakyThrows
    public ResponseEntity<String> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        Optional<Psu> psu = psuAuthService.getPsu(psuAuthBody.getId());
        if (!psu.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = generateToken(psu.get().getUserId(), getPrivateKey());

        String sessionCookieString = ResponseCookie.from(AUTHORIZATION_SESSION_ID, jwtToken)
                .httpOnly(cookieProperties.isHttpOnly())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .path(cookieProperties.getPath())
                .maxAge(cookieProperties.getMaxAge())
                .build().toString();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(X_REQUEST_ID, xRequestID.toString())
                .header(SET_COOKIE, sessionCookieString)
                .header(X_XSRF_TOKEN, getHash(jwtToken))
                .body(jwtToken);
    }

    @Override
    public ResponseEntity<Void> registration(PsuAuthBody psuAuthDto, UUID xRequestID) {
        Optional<Psu> psu = psuAuthService.createPsuIfNotExist(psuAuthDto.getId(), psuAuthDto.getPassword());
        if (!psu.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.LOCATION, "/login");
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @SneakyThrows
    private String generateToken(String id, PrivateKey privateKey) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(Date.from(currentTime.plus(WEEK).toInstant()))
                .issueTime(Date.from(currentTime.toInstant()))
                .subject(String.valueOf(id))
                .build();
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(new RSASSASigner(privateKey));
        return signedJWT.serialize();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair.getPrivate();
    }

    @NotNull
    private String getHash(String jwtToken) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jwtToken.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hash));
    }
}
