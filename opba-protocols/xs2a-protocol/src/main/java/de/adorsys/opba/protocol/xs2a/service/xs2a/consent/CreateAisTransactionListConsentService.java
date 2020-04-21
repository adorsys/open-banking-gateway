package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded.CreateConsentErrorSink;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Initiates Transaction list consent by sending mapped {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent}
 * from the context to ASPSP API.
 */
@Slf4j
@Service("xs2aTransactionListConsentInitiate")
@RequiredArgsConstructor
public class CreateAisTransactionListConsentService extends ValidatedExecution<TransactionListXs2aContext> {

    private final AisConsentInitiateExtractor extractor;
    private final AccountInformationService ais;
    private final Xs2aValidator validator;
    private final ProtocolConfiguration configuration;
    private final CreateConsentErrorSink errorSink;
    private final CreateAisConsentService createAisConsentService;

    @Override
    protected void doPrepareContext(DelegateExecution execution, TransactionListXs2aContext context) {
        context.setRedirectUriOk(
                ContextUtil.evaluateSpelForCtx(configuration.getRedirect().getConsentAccounts().getOk(), execution, context)
        );
        context.setRedirectUriNok(
                ContextUtil.evaluateSpelForCtx(configuration.getRedirect().getConsentAccounts().getNok(), execution, context)
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, TransactionListXs2aContext context) {
        context.setClassName(this.getClass().getName());
        validator.validate(execution, context, extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        ValidatedHeadersBody<ConsentInitiateHeaders, Consents> params = extractor.forExecution(context);
        errorSink.swallowConsentCreationErrorForLooping(
                () -> createAisConsentService.createConsent(ais, execution, context, params),
                ex -> createAisConsentService.aisOnWrongIban(execution, log)
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }
}
