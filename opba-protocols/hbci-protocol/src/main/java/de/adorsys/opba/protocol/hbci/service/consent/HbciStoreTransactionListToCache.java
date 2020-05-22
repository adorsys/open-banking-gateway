package de.adorsys.opba.protocol.hbci.service.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciStoreTransactionListToCache")
@RequiredArgsConstructor
public class HbciStoreTransactionListToCache extends ValidatedExecution<TransactionListHbciContext> {

    private final FlowableObjectMapper mapper;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        ProtocolFacingConsent consent = context.consentAccess().createDoNotPersist();

        consent.setConsentContext(
                mapper.writeValueAsString(
                        ImmutableMap.of(
                                context.getResponse().getClass().getCanonicalName(),
                                context.getResponse()
                        )
                )
        );

        consent.setConsentId(context.getSagaId());
        context.consentAccess().save(consent);
    }
}
