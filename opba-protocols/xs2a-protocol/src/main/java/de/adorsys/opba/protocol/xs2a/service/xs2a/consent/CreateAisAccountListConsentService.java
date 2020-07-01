package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Initiates Account list consent by sending mapped {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent}
 * from the context to ASPSP API.
 */
@Slf4j
@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class CreateAisAccountListConsentService extends ValidatedExecution<AccountListXs2aContext> {

    private final AisConsentInitiateExtractor extractor;
    private final AccountInformationService ais;
    private final Xs2aValidator validator;
    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final CreateAisConsentService createAisConsentService;

    @Override
    protected void doPrepareContext(DelegateExecution execution, AccountListXs2aContext context) {
        context.setRedirectUriOk(
                UriComponentsBuilder.fromHttpUrl(urlsConfiguration.getAis().getWebHooks().getOk())
                        .queryParam("redirectCode", context.getAspspRedirectCode())
                        .buildAndExpand(ImmutableMap.of("sessionId", context.getAuthorizationSessionIdIfOpened()))
                        .toUriString()
        );
        context.setRedirectUriNok(
                UriComponentsBuilder.fromHttpUrl(urlsConfiguration.getAis().getWebHooks().getNok())
                        .queryParam("redirectCode", context.getAspspRedirectCode())
                        .buildAndExpand(ImmutableMap.of("sessionId", context.getAuthorizationSessionIdIfOpened()))
                        .toUriString()
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, AccountListXs2aContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListXs2aContext context) {
        ValidatedHeadersBody<ConsentInitiateHeaders, Consents> params = extractor.forExecution(context);
        handler.tryCreateAndHandleErrors(execution, () -> createAisConsentService.createConsent(ais, execution, context, params));
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, AccountListXs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }
}
