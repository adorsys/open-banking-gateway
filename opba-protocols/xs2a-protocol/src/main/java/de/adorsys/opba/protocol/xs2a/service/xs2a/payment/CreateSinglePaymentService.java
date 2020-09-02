package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentOrPaymentPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.PaymentInitiationRequestResponse;
import de.adorsys.xs2a.adapter.service.model.SinglePaymentInitiationBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.adapter.link.bg.template.LinksTemplate.SCA_OAUTH;

/**
 * Initiates Account list consent by sending mapped {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent}
 * from the context to ASPSP API.
 */
@Slf4j
@Service("xs2aSinglePaymentInitiate")
@RequiredArgsConstructor
public class CreateSinglePaymentService extends ValidatedExecution<Xs2aPisContext> {

    private final PaymentInitiationService pis;
    private final Xs2aValidator validator;
    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final Extractor extractor;

    @Override
    protected void doPrepareContext(DelegateExecution execution, Xs2aPisContext context) {
        context.setRedirectUriOk(
                ContextUtil.evaluateSpelForCtx(urlsConfiguration.getPis().getWebHooks().getOk(), execution, context)
        );
        context.setRedirectUriNok(
                ContextUtil.evaluateSpelForCtx(urlsConfiguration.getPis().getWebHooks().getNok(), execution, context)
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        ValidatedPathHeadersBody<Xs2aInitialPaymentParameters, PaymentInitiateHeaders, SinglePaymentInitiationBody> params = extractor.forExecution(context);
        handler.tryCreateAndHandleErrors(execution, () -> initiatePayment(execution, context, params));
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aPisContext context) {
        context.setPaymentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
    }

    private void initiatePayment(
            DelegateExecution execution,
            Xs2aPisContext context,
            ValidatedPathHeadersBody<Xs2aInitialPaymentParameters, PaymentInitiateHeaders, SinglePaymentInitiationBody> params) {

        Response<PaymentInitiationRequestResponse> paymentInit = pis.initiateSinglePayment(
                params.getPath().getPaymentProduct(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
                params.getBody()
        );

        context.setWrongAuthCredentials(false);
        context.setPaymentId(paymentInit.getBody().getPaymentId());
        if (context.getStartScaProcessResponse().getLinks().containsKey(SCA_OAUTH)) {
            context.setOauth2IntegratedNeeded(true);
            context.setScaOauth2Link(context.getStartScaProcessResponse().getLinks().get(SCA_OAUTH).getHref());
        }
        execution.setVariable(CONTEXT, context);
    }

    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
                                                                                   Xs2aInitialPaymentParameters,
                                                                               PaymentInitiateHeaders,
                                                                               PaymentInitiateBody,
                                                                               SinglePaymentInitiationBody> {

        public Extractor(
                DtoMapper<Xs2aPisContext, PaymentInitiateBody> toValidatableBody,
                DtoMapper<PaymentInitiateBody, SinglePaymentInitiationBody> toBody,
                DtoMapper<Xs2aPisContext, PaymentInitiateHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aInitialPaymentParameters> toParameters) {

            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
