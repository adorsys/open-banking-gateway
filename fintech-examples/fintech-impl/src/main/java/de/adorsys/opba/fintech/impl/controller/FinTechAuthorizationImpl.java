package de.adorsys.opba.fintech.impl.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.api.model.InlineResponse200;
import de.adorsys.opba.fintech.api.model.LoginRequest;
import de.adorsys.opba.fintech.api.model.UserProfile;
import de.adorsys.opba.fintech.api.resource.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class FinTechAuthorizationImpl implements FinTechAuthorizationApi {

    @Autowired
    AuthorizeService authorizeService;

    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        log.info("getObjecteMapper is called");
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return Optional.of(mapper);
    }

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.info("loginPost is called");
        Optional<UserProfile> userProfile = authorizeService.findUser(loginRequest);
        if (userProfile.isPresent()) {
            InlineResponse200 response = new InlineResponse200();
            response.setUserProfile(userProfile.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
