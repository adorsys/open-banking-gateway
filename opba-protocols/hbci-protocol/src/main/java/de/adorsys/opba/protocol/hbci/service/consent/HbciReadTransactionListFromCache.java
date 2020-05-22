package de.adorsys.opba.protocol.hbci.service.consent;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service("hbciReadTransactionListFromCache")
@RequiredArgsConstructor
public class HbciReadTransactionListFromCache extends ValidatedExecution<TransactionListHbciContext> {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @SneakyThrows
    private void convertConsentToResponseIfPresent(DelegateExecution execution, TransactionListHbciContext context) {
        Optional<ProtocolFacingConsent> consent = context.consentAccess().findByCurrentServiceSession();

        if (!consent.isPresent() || null == consent.get().getConsentContext()) {
            return;
        }

        ProtocolFacingConsent target = consent.get();

        JsonNode value = mapper.readTree(target.getConsentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        AisListTransactionsResult response = (AisListTransactionsResult) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (TransactionListHbciContext toUpdate) -> toUpdate.setResponse(response)
        );
    }
}
