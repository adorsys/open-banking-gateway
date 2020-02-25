package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aFlowableProperties;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Service("xs2aLoadConsentAndContextFromDb")
@RequiredArgsConstructor
public class Xs2aLoadConsentAndContextFromDb extends ValidatedExecution<Xs2aContext> {

    private final ContextMerger merger;
    private final Xs2aFlowableProperties properties;
    private final Xs2aObjectMapper mapper;
    private final ConsentRepository consentRepository;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        loadContext(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        loadContext(execution, context);
    }

    @SneakyThrows
    private void loadContext(DelegateExecution execution, Xs2aContext context) {
        Optional<Consent> consent = consentRepository.findByServiceSessionId(context.getServiceSessionId());

        if (!consent.isPresent()) {
            return;
        }

        JsonNode value = mapper.readTree(consent.get().getContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Object ctx = mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        // TODO - tidy up context merging
        if (ctx instanceof TransactionListXs2aContext && context instanceof TransactionListXs2aContext) {
            merger.merge((TransactionListXs2aContext) context, (TransactionListXs2aContext) ctx);
        } else if (ctx instanceof TransactionListXs2aContext) {
            merger.merge(context, (TransactionListXs2aContext) ctx);
        } else if (ctx instanceof Xs2aContext) {
            merger.merge(context, (Xs2aContext) ctx);
        }

        execution.setVariable(CONTEXT, ctx);
    }

    @Mapper(
            componentModel = SPRING_KEYWORD,
            implementationPackage = XS2A_MAPPERS_PACKAGE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    public interface ContextMerger {

        void merge(Xs2aContext source, @MappingTarget Xs2aContext target);
        void merge(Xs2aContext source, @MappingTarget TransactionListXs2aContext target);
        void merge(TransactionListXs2aContext source, @MappingTarget TransactionListXs2aContext target);
    }
}
