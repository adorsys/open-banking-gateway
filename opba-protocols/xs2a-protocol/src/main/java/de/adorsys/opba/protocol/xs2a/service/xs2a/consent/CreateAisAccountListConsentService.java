package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.model.Consents;
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

    private final AisConsentInitiateExtractor extractor;
    private final AccountInformationService ais;
    private final Xs2aValidator validator;
    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final CreateAisConsentService createAisConsentService;

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
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);

        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListXs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ValidatedPathHeadersBody<ConsentInitiateParameters, ConsentInitiateHeaders, Consents> params = extractor.forExecution(context);
        var result = handler.tryCreateAndHandleErrors(execution, () -> createAisConsentService.createConsent(ais, context, params));
        if (null == result) {
            execution.setVariable(CONTEXT, context);
            log.warn("Consent creation failed");
            return;
        }

        postHandleCreatedConsent(result, execution, context);
    }
}
