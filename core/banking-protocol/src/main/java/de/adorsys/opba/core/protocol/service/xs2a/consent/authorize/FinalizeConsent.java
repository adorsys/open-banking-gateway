package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.domain.entity.Consent;
import de.adorsys.opba.core.protocol.repository.jpa.ConsentRepository;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.service.xs2a.consent.ConsentConst.CONSENT_INIT;
import static de.adorsys.opba.core.protocol.service.xs2a.consent.ConsentConst.START_SCA;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.ACCEPT;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Service("finalizeConsent")
@RequiredArgsConstructor
public class FinalizeConsent implements JavaDelegate {

    private final AccountInformationService ais;
    private final ConsentRepository consents;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        StartScaProcessResponse start = delegateExecution.getVariable(START_SCA, StartScaProcessResponse.class);
        ConsentCreationResponse consent = delegateExecution.getVariable(CONSENT_INIT, ConsentCreationResponse.class);

        Response<ScaStatusResponse> update = ais.updateConsentsPsuData(
                consent.getConsentId(),
                start.getAuthorisationId(),
                RequestHeaders.fromMap(
                        ImmutableMap.<String, String>builder()
                                .put(ACCEPT, "application/json")
                                .put(PSU_ID, "anton.brueckner")
                                .put(X_REQUEST_ID, "2f77a125-aa7a-45c0-b414-cea25a116035")
                                .put(CONTENT_TYPE, "application/json")
                                // Identifies bank for XS2A-adapter
                                .put(X_GTW_ASPSP_ID, "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                .build()
                ),
                authentication()
        );

        consents.save(Consent.builder().consentCode(consent.getConsentId()).build());
    }

    private TransactionAuthorisation authentication() {
        TransactionAuthorisation authorisation = new TransactionAuthorisation();
        authorisation.setScaAuthenticationData("123456");
        return authorisation;
    }
}
