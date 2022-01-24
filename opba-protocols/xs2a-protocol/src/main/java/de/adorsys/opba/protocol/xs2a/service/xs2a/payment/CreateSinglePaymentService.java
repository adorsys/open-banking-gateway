package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.StartAuthorizationHandlerUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2.OAuth2Util;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentInitationRequestResponse201;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.api.ResponseHeaders.ASPSP_SCA_APPROACH;

/**
 * Initiates Account list consent by sending mapped {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent}
 * from the context to ASPSP API.
 */
@Slf4j
@Service("xs2aSinglePaymentInitiate")
@RequiredArgsConstructor
public class CreateSinglePaymentService extends ValidatedExecution<Xs2aPisContext> {
    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final SinglePaymentInitiationServiceProvider initiationServiceProvider;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doPrepareContext(DelegateExecution execution, Xs2aPisContext context) {
        context.setRedirectUriOk(
                ContextUtil.buildAndExpandQueryParameters(urlsConfiguration.getPis().getWebHooks().getOk(), context).toASCIIString()
        );
        context.setRedirectUriNok(
                ContextUtil.buildAndExpandQueryParameters(urlsConfiguration.getPis().getWebHooks().getNok(), context).toASCIIString()
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        initiationServiceProvider.instance(context).doValidate(execution, context);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        var result = initiationServiceProvider.instance(context).doExecution(execution, context);
        if (null == result) {
            execution.setVariable(CONTEXT, context);
            log.warn("Payment creation failed");
            return;
        }
        postHandleCreatedPayment(result, execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        context.setPaymentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }

    protected void postHandleCreatedPayment(Response<PaymentInitationRequestResponse201> paymentInit, DelegateExecution execution, Xs2aPisContext context) {
        context.setWrongAuthCredentials(false);
        context.setPaymentId(paymentInit.getBody().getPaymentId());
        if (null != paymentInit.getBody()) {
            OAuth2Util.handlePossibleOAuth2(paymentInit.getBody().getLinks(), context);
            StartAuthorizationHandlerUtil.handleImplicitAuthorizationStartIfPossible(paymentInit.getBody().getLinks(), context);
        }

        if (null != paymentInit.getHeaders() && Strings.isNotBlank(paymentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH))) {
            context.setAspspScaApproach(paymentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH));
            if (null != paymentInit.getBody()) {
                context.setConsentOrPaymentCreateLinks(paymentInit.getBody().getLinks());
            }
        }
        execution.setVariable(CONTEXT, context);
    }

}
