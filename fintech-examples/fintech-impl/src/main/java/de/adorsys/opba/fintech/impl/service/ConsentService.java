package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppConsentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsentService {

    private final TppConsentClient tppConsentClient;
    private final TppProperties tppProperties;
    private final ConsentRepository consentRepository;

    public boolean confirmConsent(String authId, UUID xRequestId) {
        HttpStatus statusCode = tppConsentClient.confirmConsent(
                authId,
                xRequestId,
                tppProperties.getServiceSessionPassword(),
                COMPUTE_X_TIMESTAMP_UTC,
                OperationType.CONFIRM_CONSENT.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID
        ).getStatusCode();
        log.debug("consent confirmation response code: {}", statusCode);
        return statusCode.is2xxSuccessful();
    }

    public void deleteConsent(UserEntity userEntity, ConsentType consentType, String bankID) {
        Optional<ConsentEntity> optionalConsent = consentRepository.findByUserEntityAndBankIdAndConsentTypeAndConsentConfirmed(userEntity,
                bankID, consentType, Boolean.TRUE);
        if (optionalConsent.isPresent()) {
            log.info("consent {} for user {} is deleted", consentType, userEntity.getLoginUserName());
            consentRepository.delete(optionalConsent.get());
        }
    }

}
