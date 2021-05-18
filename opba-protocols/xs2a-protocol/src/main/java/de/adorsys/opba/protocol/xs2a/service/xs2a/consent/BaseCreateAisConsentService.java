package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2.OAuth2Util;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;

import java.util.UUID;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.api.ResponseHeaders.ASPSP_SCA_APPROACH;

public abstract class BaseCreateAisConsentService<T extends Xs2aContext> extends ValidatedExecution<T> {

    protected final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doMockedExecution(DelegateExecution execution, T context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }

    protected void postHandleCreatedConsent(Response<ConsentsResponse201> consentInit, DelegateExecution execution, Xs2aContext context) {
        context.setWrongAuthCredentials(false);
        context.setConsentId(consentInit.getBody().getConsentId());
        if (null != consentInit.getBody()) {
            handleOAuthIfPossible(consentInit, context);
        }

        if (null != consentInit.getHeaders() && Strings.isNotBlank(consentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH))) {
            context.setAspspScaApproach(consentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH));
            if (null != consentInit.getBody()) {
                context.setConsentOrPaymentCreateLinks(consentInit.getBody().getLinks());
            }
        }

        execution.setVariable(CONTEXT, context);
    }

    private void handleOAuthIfPossible(Response<ConsentsResponse201> consentInit, Xs2aContext context) {
        var links = consentInit.getBody().getLinks();
        if (null == links && null == consentInit.getBody().getConsentId()) { // NOTE that PIS does not contain similar logic (per ING bank impl)
            context.setOauth2IntegratedNeeded(true);
            context.setOauth2ConsentNeeded(true);
        } else {
            OAuth2Util.handlePossibleOAuth2(links, context);
        }
    }
}
