package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.TppConsentClient;
import de.adorsys.opba.fintech.impl.tppclients.TppPaymentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsentService {

    private final TppPaymentClient tppPaymentClient;
    private final TppConsentClient tppConsentClient;
    private final TppProperties tppProperties;
    private final ConsentRepository consentRepository;

    public boolean confirmConsent(String authId, UUID xRequestId) {
        HttpStatusCode statusCode = tppConsentClient.confirmConsent(
                authId,
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                null,
                tppProperties.getFintechDataProtectionPassword()
        ).getStatusCode();
        log.debug("consent confirmation response code: {}", statusCode);
        return statusCode.is2xxSuccessful();
    }

    public boolean confirmPayment(String authId, UUID xRequestId) {
        HttpStatusCode statusCode = tppPaymentClient.confirmPayment(
                authId,
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                null,
                tppProperties.getFintechDataProtectionPassword()
        ).getStatusCode();
        log.debug("consent confirmation response code: {}", statusCode);
        return statusCode.is2xxSuccessful();
    }

    @Transactional
    public void deleteAllConsentsOfBank(SessionEntity sessionEntity, String bankId) {
        consentRepository.deleteByUserEntityAndBankId(sessionEntity.getUserEntity(), bankId);
    }
}
