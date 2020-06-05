package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.LoginEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.database.repositories.LoginRepository;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ConsentService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.fintech.impl.service.SessionLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechAuthorizationImpl implements FinTechAuthorizationApi {

    private final AuthorizeService authorizeService;
    private final RedirectHandlerService redirectHandlerService;
    private final RestRequestContext restRequestContext;
    private final ConsentService consentService;
    private final LoginRepository loginRepository;
    private final ConsentRepository consentRepository;
    private final SessionLogicService sessionLogicService;

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.debug("loginPost is called for {}", loginRequest.getUsername());
        Optional<UserEntity> optionalUserEntity = authorizeService.login(loginRequest);
        if (!optionalUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserEntity userEntity = optionalUserEntity.get();
        InlineResponse200 response = new InlineResponse200();
        UserProfile userProfile = new UserProfile();
        userProfile.setName(userEntity.getLoginUserName());

        Iterator<LoginEntity> loginEntitiesIterator = loginRepository.findByUserEntityOrderByLoginTimeDesc(userEntity).iterator();
        if (loginEntitiesIterator.hasNext()) {
            userProfile.setLastLogin(loginEntitiesIterator.next().getLoginTime());
            log.info("last login for user {} was {}", userEntity.getLoginUserName(), userProfile.getLastLogin());
        } else {
            log.info("this was very first login for user {}", userEntity.getLoginUserName());
        }
        response.setUserProfile(userProfile);
        loginRepository.save(new LoginEntity(userEntity));

        HttpHeaders responseHeaders = sessionLogicService.login(userEntity);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> fromConsentGET(String authId, String okOrNotokString, String finTechRedirectCode, UUID xRequestID, String xsrfToken) {
        OkOrNotOk okOrNotOk = OkOrNotOk.valueOf(okOrNotokString);
        if (!sessionLogicService.isRedirectAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (okOrNotOk.equals(OkOrNotOk.OK) && consentService.confirmConsent(authId, xRequestID)) {

            Optional<ConsentEntity> consent = consentRepository.findByAuthId(authId);

            if (!consent.isPresent()) {
                throw new RuntimeException("consent for authid " + authId + " can not be found");
            }

            log.debug("consent with authId {} is now valid", authId);
            consent.get().setConsentConfirmed(true);
            consentRepository.save(consent.get());
        }
        return sessionLogicService.addSessionMaxAgeToHeader(
                redirectHandlerService.doRedirect(authId, finTechRedirectCode, okOrNotOk));
    }

    @Override
    public ResponseEntity<Void> logoutPOST(UUID xRequestID, String xsrfToken) {
        log.debug("logoutPost is called for {}", restRequestContext);

        if (!sessionLogicService.isSessionAuthorized()) {
            log.warn("logoutPOST failed: user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        authorizeService.logout();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, restRequestContext.getRequestId());
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }

}
