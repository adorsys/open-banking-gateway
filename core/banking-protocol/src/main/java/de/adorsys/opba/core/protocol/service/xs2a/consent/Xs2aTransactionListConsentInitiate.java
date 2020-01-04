package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentsBody;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.Xs2aConsentInitiate;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aTransactionListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aTransactionListConsentInitiate extends ValidatedExecution<TransactionListXs2aContext> {

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
        Xs2aConsentInitiate consent = consentInitiate(context);
        validator.validate(execution, consent.getHeaders(), consent.getBody()); // flatten path
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        Xs2aConsentInitiate consent = consentInitiate(context);
        Response<ConsentCreationResponse> consentInit = ais.createConsent(
            consent.getHeaders().toHeaders(),
            ConsentsBody.TO_XS2A.map(consent.getBody())
        );

        context.setConsentId(consentInit.getBody().getConsentId());
        execution.setVariable(CONTEXT, context);
    }

    private Xs2aConsentInitiate consentInitiate(Xs2aContext context) {
        return new Xs2aConsentInitiate(
            ConsentInitiateHeaders.XS2A_HEADERS.map(context),
            ConsentsBody.FROM_CTX.map(context)
        );
    }
}
