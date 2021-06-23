package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.ConsentContextLoadingService;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Loads the associated context with the consent from databases and merges it with current context. The process
 * continues with merged context.
 * Merging is necessary in order to work with i.e. dedicated consent as it is possible to validate
 * IBAN list the consent was granted for.
 */
@Service("xs2aLoadConsentAndContextFromDb")
@RequiredArgsConstructor
public class Xs2aLoadConsentAndContextFromDb extends ValidatedExecution<Xs2aAisContext> {

    private final ContextMerger merger;
    private final ConsentContextLoadingService contextLoader;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aAisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        loadContext(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aAisContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        loadContext(execution, context);
    }

    @SneakyThrows
    private void loadContext(DelegateExecution execution, Xs2aAisContext context) {
        Optional<ProtocolFacingConsent> consent = context.consentAccess().findSingleByCurrentServiceSession();

        if (!consent.isPresent() || null == consent.get().getConsentContext()) {
            return;
        }

        Xs2aAisContext ctx = contextLoader.contextFromConsent(consent);

        // TODO - tidy up context merging
        if (ctx instanceof TransactionListXs2aContext && context instanceof TransactionListXs2aContext) {
            merger.merge((TransactionListXs2aContext) context, (TransactionListXs2aContext) ctx);
        } else if (ctx instanceof AccountListXs2aContext && context instanceof TransactionListXs2aContext) { // allPsd2 handling
            merger.merge((AccountListXs2aContext) ctx, (TransactionListXs2aContext) context);
            // original request was for account listing but now is for transaction listing
            ctx = context;
        } else if (ctx instanceof TransactionListXs2aContext) {
            merger.merge(context, (TransactionListXs2aContext) ctx);
        } else {
            merger.merge(context, ctx);
        }

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
        @Mapping(target = "consentAcquired", ignore = true)
        void merge(Xs2aAisContext source, @MappingTarget Xs2aAisContext target);

        @Mapping(target = "mode", ignore = true)
        @Mapping(target = "flowByAction", ignore = true)
        @Mapping(target = "psuPassword", ignore = true)
        @Mapping(target = "lastScaChallenge", ignore = true)
        @Mapping(target = "consentAcquired", ignore = true)
        void merge(Xs2aAisContext source, @MappingTarget TransactionListXs2aContext target);

        @Mapping(target = "mode", ignore = true)
        @Mapping(target = "flowByAction", ignore = true)
        @Mapping(target = "psuPassword", ignore = true)
        @Mapping(target = "lastScaChallenge", ignore = true)
        @Mapping(target = "consentAcquired", ignore = true)
        void merge(TransactionListXs2aContext source, @MappingTarget TransactionListXs2aContext target);

        @Mapping(target = "mode", ignore = true)
        @Mapping(target = "flowByAction", ignore = true)
        @Mapping(target = "psuPassword", ignore = true)
        @Mapping(target = "lastScaChallenge", ignore = true)
        @Mapping(target = "consentAcquired", ignore = true)
        void merge(AccountListXs2aContext source, @MappingTarget TransactionListXs2aContext target);
    }
}
