package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class FinTechAuthorizationImpl implements FinTechAuthorizationApi {

    private static final String X_REQUEST_ID = "X-Request-ID";

    @Autowired
    AuthorizeService authorizeService;

    @Autowired
    CookieConfigProperties cookieConfigProperties;

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.info("loginPost is called");
        Optional<UserEntity> userEntity = authorizeService.findUser(loginRequest);
        if (userEntity.isPresent()) {

            InlineResponse200 response = new InlineResponse200();
            response.setUserProfile(userEntity.get().getUserProfile());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(X_REQUEST_ID, xRequestID.toString());
            userEntity.get().getCookies().forEach(cookie -> {
                responseHeaders.set(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from(cookie.getName(), cookie.getValue())
                                .httpOnly(cookieConfigProperties.isHttpOnly())
                                .sameSite(cookieConfigProperties.getSameSite())
                                .secure(cookieConfigProperties.isSecure())
                                .path(cookieConfigProperties.getPath())
                                .maxAge(cookieConfigProperties.getMaxAge())
                                .build().toString());
            });
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
