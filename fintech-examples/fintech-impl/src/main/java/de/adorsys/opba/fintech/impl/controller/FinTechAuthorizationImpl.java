package de.adorsys.opba.fintech.impl.controller;

import com.google.common.base.Strings;
import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.controller.utils.OkOrNotOk;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.LoginEntity;
import de.adorsys.opba.fintech.impl.database.entities.PaymentEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.database.repositories.LoginRepository;
import de.adorsys.opba.fintech.impl.database.repositories.PaymentRepository;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ConsentService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.fintech.impl.service.SessionLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
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
    private final PaymentRepository paymentRepository;
    private final SessionLogicService sessionLogicService;

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(UUID xRequestID, LoginRequest loginRequest) {
        log.debug("loginPost is called for {}", loginRequest.getUsername());
        Optional<UserEntity> optionalUserEntity = authorizeService.loginWithPassword(loginRequest);
        if (!optionalUserEntity.isPresent()) {
            log.info("Wrong password for user : {}", loginRequest.getUsername());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserEntity userEntity = optionalUserEntity.get();
        return performUserLogin(userEntity);
    }

    @Override
    @Transactional
    public ResponseEntity<InlineResponse200> callbackGetLogin(String code, String state, String scope, String error) {
        if (!Strings.isNullOrEmpty(error)) {
            throw new IllegalStateException(String.format("Resource server returned error: %s for %s", error, state));
        }

        UserEntity user = authorizeService.loginWithOAuth2(code, state, restRequestContext.getOauth2StateCookieValue());
        return performUserLogin(user);
    }

    @Override
    public ResponseEntity<Void> fromConsentGET(String authId,
                                               String okOrNotokString,
                                               String finTechRedirectCode,
                                               UUID xRequestID,
                                               String xsrfToken) {
        OkOrNotOk okOrNotOk = OkOrNotOk.valueOf(okOrNotokString);

        if (!sessionLogicService.isRedirectAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<ConsentEntity> consent = consentRepository.findByTppAuthId(authId);

        if (!consent.isPresent()) {
            throw new RuntimeException("consent for authid " + authId + " can not be found");
        }

        ConsentEntity consentEntity = consent.get();

        if (okOrNotOk.equals(OkOrNotOk.OK) && consentService.confirmConsent(authId, xRequestID)) {

            this.handleConsentConfirmation(authId, consentEntity);

        } else {
            if (Boolean.TRUE.equals(consentEntity.getConsentConfirmed())) {
                log.info("Ignore cancel {} {}", consentEntity.getConsentType(), consentEntity.getCreationTime());
            } else {
                log.info("Delete consent {} {}", consentEntity.getConsentType(), consentEntity.getCreationTime());
                consentRepository.delete(consentEntity);
            }
        }

        return sessionLogicService.addSessionMaxAgeToHeader(
                redirectHandlerService.doRedirect(authId, finTechRedirectCode, okOrNotOk));
    }

    @Override
    public ResponseEntity<Void> fromPaymentGET(String authId, String okOrNotokString, String finTechRedirectCode, UUID xRequestID, String xsrfToken) {
        OkOrNotOk okOrNotOk = OkOrNotOk.valueOf(okOrNotokString);
        if (!sessionLogicService.isRedirectAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (okOrNotOk.equals(OkOrNotOk.OK) && consentService.confirmPayment(authId, xRequestID)) {

            Optional<PaymentEntity> payment = paymentRepository.findByTppAuthId(authId);

            if (!payment.isPresent()) {
                throw new RuntimeException("consent for authid " + authId + " can not be found");
            }

            log.debug("consent with authId {} is now valid", authId);
            payment.get().setPaymentConfirmed(true);
            paymentRepository.save(payment.get());
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

    private ResponseEntity<InlineResponse200> performUserLogin(UserEntity userEntity) {
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

    private void handleConsentConfirmation(String authId, ConsentEntity consentEntity) {
        // There may exist a valid consent that has not been used, because client
        // wanted to retrieve new consent. So we search all valid consents and delete them.
        List<ConsentEntity> consentList = consentRepository.findListByUserEntityAndBankIdAndConsentTypeAndConsentConfirmed(
                consentEntity.getUserEntity(),
                consentEntity.getBankId(),
                consentEntity.getConsentType(),
                Boolean.TRUE);
        for (ConsentEntity oldValidConsent : consentList) {
            if (Boolean.TRUE.equals(oldValidConsent.getConsentConfirmed())) {
                log.warn("Consent created at \" + {} + \" must not be confirmed yet (but is OK for HBCI))", oldValidConsent.getCreationTime());
            } else {
                log.debug("delete old valid {} consent from {}", oldValidConsent.getConsentType(), oldValidConsent.getCreationTime());
//                    consentRepository.delete(oldValidConsent); Garbage-collection  in fintech-server for HBCI
            }
        }
        log.info("consent with authId {} is now valid", authId);
        consentEntity.setConsentConfirmed(true);
        consentRepository.save(consentEntity);
    }
}
