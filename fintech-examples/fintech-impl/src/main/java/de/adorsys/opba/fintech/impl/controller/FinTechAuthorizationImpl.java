package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.info("loginPost is called");
        Boolean httpOnly = false;
        Boolean secure = false;
        final String stringInfix = httpOnly ? " HttpOnly;" : "" + (secure ? " Secure;" : "");
        Optional<AuthorizeService.UserEntity> userEntity = authorizeService.findUser(loginRequest);
        if (userEntity.isPresent()) {
            InlineResponse200 response = new InlineResponse200();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(X_REQUEST_ID, xRequestID.toString());
            userEntity.get().getCookies().forEach(cookie -> {
                String newCookie = cookie + ";" + stringInfix + " Path=/";
                log.info("set cookie to {}", newCookie);
                responseHeaders.set(HttpHeaders.SET_COOKIE, newCookie);
            });
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
