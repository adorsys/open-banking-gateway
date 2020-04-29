package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Loads the associated context with the payment from databases and merges it with current context. The process
 * continues with merged context.
 * Merging is necessary in order to work with i.e. dedicated consent as it is possible to validate
 * IBAN list the consent was granted for.
 */
@Service("xs2aLoadPaymentAndContextFromDb")
@RequiredArgsConstructor
public class Xs2aLoadPaymentAndContextFromDb extends ValidatedExecution<Xs2aPisContext> {

    private final ContextMerger merger;
    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        loadContext(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aPisContext context) {
        loadContext(execution, context);
    }

    @SneakyThrows
    private void loadContext(DelegateExecution execution, Xs2aPisContext context) {
        Optional<ProtocolFacingConsent> pisConsent = context.consentAccess().findByCurrentServiceSession();

        if (!pisConsent.isPresent() || null == pisConsent.get().getConsentContext()) {
            return;
        }

        ProtocolFacingConsent target = pisConsent.get();

        JsonNode value = mapper.readTree(target.getConsentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Xs2aPisContext ctx = (Xs2aPisContext) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        ctx.setConsentId(target.getConsentId());

        // TODO  DEBUG merger

       /* ctx.setPaymentId(target.getPaymentId());
        ctx.setPaymentProduct(target.getPaymentContext());
        ctx.setPaymentType(target.getPaymentType());*/
        // TODO =============

        merger.merge(context, ctx);

        // Avoid ignoring MOCK mode due to Merged context received REAL mode
        ctx.setMode(context.getMode());
        execution.setVariable(CONTEXT, ctx);
    }

    @Mapper(
            componentModel = SPRING_KEYWORD,
            implementationPackage = XS2A_MAPPERS_PACKAGE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    public interface ContextMerger {

        @Mapping(target = "mode", ignore = true)
        @Mapping(target = "flowByAction", ignore = true)
        @Mapping(target = "psuPassword", ignore = true)
        @Mapping(target = "lastScaChallenge", ignore = true)
        void merge(Xs2aContext source, @MappingTarget Xs2aContext target);
    }
}
