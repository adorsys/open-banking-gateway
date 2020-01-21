package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateBody;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aAccountListConsentInitiate extends ValidatedExecution<Xs2aContext> {

    private final ConsentInitiateBody.ToXs2aApi toBody;
    private final ConsentInitiateBody.FromCtx toValidatableBody;
    private final ConsentInitiateHeaders.FromCtx toHeaders;
    private final AccountInformationService ais;
    private final Xs2aValidator validator;
    private final ProtocolConfiguration configuration;

    @Override
    protected void doPrepareContext(DelegateExecution execution, Xs2aContext context) {
        context.setRedirectUriOk(
                ContextUtil.evaluateSpelForCtx(configuration.getRedirect().getConsentAccounts().getOk(), execution, context)
        );
        context.setRedirectUriNok(
                ContextUtil.evaluateSpelForCtx(configuration.getRedirect().getConsentAccounts().getNok(), execution, context)
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, toHeaders.map(context), toValidatableBody.map(context)); // flatten path
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<ConsentCreationResponse> consentInit = ais.createConsent(
            toHeaders.map(context).toHeaders(),
            toBody.map(context)
        );

        context.setConsentId(consentInit.getBody().getConsentId());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }
}
