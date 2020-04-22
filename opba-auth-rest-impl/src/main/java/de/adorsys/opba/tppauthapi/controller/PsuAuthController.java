package de.adorsys.opba.tppauthapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.api.security.service.TokenBasedAuthService;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.protocol.facade.config.auth.FacadeAuthConfig;
import de.adorsys.opba.protocol.facade.config.auth.UriExpandConst;
import de.adorsys.opba.protocol.facade.services.authorization.PsuLoginForAisService;
import de.adorsys.opba.protocol.facade.services.psu.PsuAuthService;
import de.adorsys.opba.tppauthapi.model.generated.LoginResponse;
import de.adorsys.opba.tppauthapi.model.generated.PsuAuthBody;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthenticationAndConsentApprovalApi;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthenticationApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //FIXME move CORS at gateway/load balancer level
public class PsuAuthController implements PsuAuthenticationApi, PsuAuthenticationAndConsentApprovalApi {

    public static final Base64.Encoder ENCODER = Base64.getEncoder();
    private final PsuLoginForAisService aisService;
    private final PsuAuthService psuAuthService;
    private final TokenBasedAuthService authService;
    private final FacadeAuthConfig authConfig;
    private final TppAuthResponseCookieTemplate tppAuthResponseCookieBuilder;

    // TODO - probably this operation is not needed. At least for simple usecase.
    @Override
    @SneakyThrows
    public ResponseEntity<LoginResponse> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        Psu psu = psuAuthService.tryAuthenticateUser(psuAuthBody.getLogin(), psuAuthBody.getPassword());

        String jwtToken = authService.generateToken(psu.getLogin());

        String cookieString = tppAuthResponseCookieBuilder
                .builder(AUTHORIZATION_SESSION_ID, jwtToken)
                .build()
                .toString();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setXsrfToken(ENCODER.encodeToString(jwtToken.getBytes()));
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(X_REQUEST_ID, xRequestID.toString())
                .header(SET_COOKIE, cookieString)
                .body(loginResponse);
    }

    @Override
    public ResponseEntity<LoginResponse> loginForApproval(PsuAuthBody body, UUID xRequestId, String redirectCode, UUID authorizationId) {
        PsuLoginForAisService.Outcome outcome = aisService.loginAndAssociateAuthSession(body.getLogin(), body.getPassword(), authorizationId, redirectCode);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(LOCATION, outcome.getRedirectLocation().toASCIIString())
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(SET_COOKIE, buildAuthorizationCookiesOnAllPaths(authorizationId, outcome.getKey()))
                .build();
    }

    @Override
    public ResponseEntity<Void> registration(PsuAuthBody psuAuthDto, UUID xRequestID) {
        psuAuthService.createPsuIfNotExist(psuAuthDto.getLogin(), psuAuthDto.getPassword());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(LOCATION, authConfig.getRedirect().getLoginPage());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    private String[] buildAuthorizationCookiesOnAllPaths(UUID authorizationId, String key) {
        String token = authService.generateToken(key);
        return authConfig.getCookie().getPathTemplates().stream()
                .map(it -> cookieString(authorizationId, it, token))
                .toArray(String[]::new);
    }

    private String cookieString(UUID authorizationId, String path, String token) {
        ResponseCookie.ResponseCookieBuilder builder = tppAuthResponseCookieBuilder
                .builder(AUTHORIZATION_SESSION_KEY,  token);

        if (!Strings.isNullOrEmpty(authConfig.getCookie().getDomain())) {
            builder = builder.domain(authConfig.getCookie().getDomain());
        }

        builder = builder.path(
                UriComponentsBuilder.fromPath(path)
                        .buildAndExpand(ImmutableMap.of(UriExpandConst.AUTHORIZATION_SESSION_ID, authorizationId.toString()))
                        .toUriString()
        );

        return builder.build().toString();
    }
}
