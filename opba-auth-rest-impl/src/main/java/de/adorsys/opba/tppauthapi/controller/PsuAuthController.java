package de.adorsys.opba.tppauthapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.api.security.internal.config.AuthorizationSessionKeyConfig;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
import de.adorsys.opba.api.security.internal.service.CookieBuilderTemplate;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.protocol.facade.config.auth.FacadeConsentAuthConfig;
import de.adorsys.opba.protocol.facade.config.auth.UriExpandConst;
import de.adorsys.opba.protocol.facade.services.authorization.PsuLoginService;
import de.adorsys.opba.protocol.facade.services.psu.PsuAuthService;
import de.adorsys.opba.tppauthapi.model.generated.LoginResponse;
import de.adorsys.opba.tppauthapi.model.generated.PsuAuthBody;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthenticationAndConsentApprovalApi;
import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthenticationApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.HttpHeaders.COOKIE_TTL;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //FIXME move CORS at gateway/load balancer level
public class PsuAuthController implements PsuAuthenticationApi, PsuAuthenticationAndConsentApprovalApi {

    public static final Base64.Encoder ENCODER = Base64.getEncoder();
    private final PsuLoginService loginService;
    private final PsuAuthService psuAuthService;
    private final TokenBasedAuthService authService;
    private final FacadeConsentAuthConfig authConfig;
    private final AuthorizationSessionKeyConfig.AuthorizationSessionKeyFromHttpRequest authorizationKeyFromHttpRequest;
    private final CookieBuilderTemplate cookieBuilderTemplate;
    private final CookieProperties cookieProperties;
    private final TppTokenProperties tppTokenProperties;

    // TODO - probably this operation is not needed. At least for simple usecase.
    @Override
    @SneakyThrows
    public CompletableFuture<ResponseEntity<LoginResponse>> login(PsuAuthBody psuAuthBody, UUID xRequestID) {
        Psu psu = psuAuthService.tryAuthenticateUser(psuAuthBody.getLogin(), psuAuthBody.getPassword());

        String jwtToken = authService.generateToken(psu.getLogin(), tppTokenProperties.getTokenValidityDuration());

        String cookieString = cookieBuilderTemplate
                .builder(jwtToken)
                .build()
                .toString();

        String ttl = Long.toString(cookieProperties.getMaxAge().getSeconds());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setXsrfToken(ENCODER.encodeToString(jwtToken.getBytes()));
        return CompletableFuture.completedFuture(
                ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .header(X_REQUEST_ID, xRequestID.toString())
                    .header(COOKIE_TTL, ttl)
                    .header(SET_COOKIE, cookieString)
                    .body(loginResponse)
        );
    }

    @Override
    public CompletableFuture loginForApproval(PsuAuthBody body, UUID xRequestId, String redirectCode, UUID authorizationId) {
        var outcome = loginService.loginInPsuScopeAndAssociateAuthSession(body.getLogin(), body.getPassword(), authorizationId, redirectCode);
        return outcome.thenApply(it -> createResponseWithSecretKeyInCookieOnAllPaths(xRequestId, authorizationId, it));
    }

    @Override
    public CompletableFuture loginForAnonymousApproval(UUID xRequestId, UUID authorizationId, String redirectCode) {
        var outcome = loginService.anonymousPsuAssociateAuthSession(authorizationId, redirectCode);
        return outcome.thenApply(it -> createResponseWithSecretKeyInCookieOnAllPaths(xRequestId, authorizationId, it));
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> registration(PsuAuthBody psuAuthDto, UUID xRequestID) {
        psuAuthService.createPsuIfNotExist(psuAuthDto.getLogin(), psuAuthDto.getPassword());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(LOCATION, authConfig.getRedirect().getConsentLogin().getPage().getForAis());
        return CompletableFuture.completedFuture(new ResponseEntity<>(responseHeaders, HttpStatus.CREATED));
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

    @Override
    public CompletableFuture<ResponseEntity<Void>> renewalAuthorizationSessionKey(UUID xRequestId, UUID authorizationId) {
        String[] cookies = buildAuthorizationCookiesOnAllPaths(authorizationId,
                                                               authorizationKeyFromHttpRequest.getKey(),
                                                               tppTokenProperties.getTokenValidityDuration());

        String ttl = Long.toString(cookieProperties.getMaxAge().getSeconds());
        log.debug("cookie is renewed for authid {} for time {}", authorizationId, ttl);
        return CompletableFuture.completedFuture(
                ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .header(X_REQUEST_ID, xRequestId.toString())
                        .header(COOKIE_TTL, ttl)
                        .header(SET_COOKIE, cookies)
                        .build()
        );
    }

    @NotNull
    private ResponseEntity<?> createResponseWithSecretKeyInCookieOnAllPaths(UUID xRequestId, UUID authorizationId, PsuLoginService.Outcome outcome) {

        if (outcome instanceof PsuLoginService.ErrorOutcome) {
            var headers = ((PsuLoginService.ErrorOutcome) outcome).getHeaders();
            var httpHeaders = new HttpHeaders();
            headers.forEach((k, v) -> httpHeaders.put(k, Collections.singletonList(v)));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaders).body(headers.get("X-ERROR-MESSAGE"));
        }

        String ttl = Long.toString(cookieProperties.getMaxAge().getSeconds());
        log.debug("created new session cookie for authid {}", authorizationId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(LOCATION, outcome.getRedirectLocation().toASCIIString())
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(COOKIE_TTL, ttl)
                .header(SET_COOKIE, buildAuthorizationCookiesOnAllPaths(authorizationId,
                        outcome.getKey(),
                        tppTokenProperties.getTokenValidityDuration()))
                .build();
    }

    private String[] buildAuthorizationCookiesOnAllPaths(UUID authorizationId, String key, Duration duration) {
        String token = authService.generateToken(key, duration);
        return authConfig.getAuthorizationSessionKey().getCookie().getPathTemplates().stream()
                .map(it -> cookieString(authorizationId, it, token))
                .toArray(String[]::new);
    }

    private String cookieString(UUID authorizationId, String path, String token) {
        String redirectPath = UriComponentsBuilder.fromPath(path)
                                      .buildAndExpand(ImmutableMap.of(UriExpandConst.AUTHORIZATION_SESSION_ID, authorizationId.toString()))
                                      .toUriString();

        String domain = authConfig.getAuthorizationSessionKey().getCookie().getDomain();

        ResponseCookie.ResponseCookieBuilder builder = cookieBuilderTemplate.builder(token, redirectPath, domain);

        return builder.build().toString();
    }
}
