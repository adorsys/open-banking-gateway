package de.adorsys.opba.protocol.hbci.service.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciStoreAccountListToCache")
@RequiredArgsConstructor
public class HbciStoreAccountListToCache extends ValidatedExecution<AccountListHbciContext> {

    private final FlowableObjectMapper mapper;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        ProtocolFacingConsent consent = context.consentAccess().createDoNotPersist();

        consent.setConsentContext(
                mapper.writeValueAsString(
                        ImmutableMap.of(
                                context.getResponse().getClass().getCanonicalName(),
                                context.getResponse()
                        )
                )
        );

        context.consentAccess().save(consent);
    }
}
