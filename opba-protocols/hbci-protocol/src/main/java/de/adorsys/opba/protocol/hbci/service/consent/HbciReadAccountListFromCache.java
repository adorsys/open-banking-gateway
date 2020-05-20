package de.adorsys.opba.protocol.hbci.service.consent;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service("hbciReadAccountListFromCache")
@RequiredArgsConstructor
public class HbciReadAccountListFromCache extends ValidatedExecution<AccountListHbciContext> {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {

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

        AisListAccountsResult response = (AisListAccountsResult) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (AccountListHbciContext toUpdate) -> toUpdate.setResponse(response)
        );
    }
}
