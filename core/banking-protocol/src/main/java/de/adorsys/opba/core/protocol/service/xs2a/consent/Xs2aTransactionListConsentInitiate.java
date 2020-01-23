package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aTransactionListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aTransactionListConsentInitiate extends ValidatedExecution<TransactionListXs2aContext> {

    private final ConsentInitiateExtractor extractor;
    private final AccountInformationService ais;
    private final Xs2aValidator validator;
    private final ProtocolConfiguration configuration;

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
        validator.validate(execution, extractor.forValidation(context)); // flatten path
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        ValidatedHeadersBody<ConsentInitiateHeaders, Consents> params = extractor.forExecution(context);
        Response<ConsentCreationResponse> consentInit = ais.createConsent(
                params.getHeaders().toHeaders(),
                params.getBody()
        );

        context.setConsentId(consentInit.getBody().getConsentId());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }
}
