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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
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
    private final HttpServletResponse httpServletResponse;
    private final CookieProperties cookieProperties;

    @Override
    @SneakyThrows
    public ResponseEntity<String> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        Optional<Psu> psu = psuAuthService.getPsu(psuAuthBody.getId());
        if (!psu.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.generateKeyPair();
        String jwtToken = generateToken(psu.get().getUserId(), keyPair.getPrivate(), WEEK);

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
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
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
