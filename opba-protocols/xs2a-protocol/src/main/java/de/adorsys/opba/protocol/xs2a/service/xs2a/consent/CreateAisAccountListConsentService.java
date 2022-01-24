package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Initiates Account list consent by sending mapped {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent}
 * from the context to ASPSP API.
 */
@Slf4j
@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class CreateAisAccountListConsentService extends BaseCreateAisConsentService<AccountListXs2aContext> {

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final  Xs2aAccountListConsentServiceProvider accountListConsentServiceProvider;

    @Override
    protected void doPrepareContext(DelegateExecution execution, AccountListXs2aContext context) {
        context.setRedirectUriOk(
                ContextUtil.buildAndExpandQueryParameters(urlsConfiguration.getAis().getWebHooks().getOk(), context).toASCIIString()
        );
        context.setRedirectUriNok(
                ContextUtil.buildAndExpandQueryParameters(urlsConfiguration.getAis().getWebHooks().getNok(), context).toASCIIString()
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, AccountListXs2aContext context) {
        accountListConsentServiceProvider.instance(context).doValidate(execution, context);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListXs2aContext context) {

        var result = accountListConsentServiceProvider.instance(context).doExecution(execution, context);
        if (null == result) {
            execution.setVariable(CONTEXT, context);
            log.warn("Consent creation failed");
            return;
        }

        postHandleCreatedConsent(result, execution, context);
    }
}
