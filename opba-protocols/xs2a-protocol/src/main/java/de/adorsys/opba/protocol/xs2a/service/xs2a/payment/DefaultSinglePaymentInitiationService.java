package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentOrPaymentPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.quirks.QuirkUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentInitationRequestResponse201;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultSinglePaymentInitiationService implements SinglePaymentInitiationService {
    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final Extractor extractor;

    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    public void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    public Response<PaymentInitationRequestResponse201> doExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        var params = extractor.forExecution(context);
        return handler.tryCreateAndHandleErrors(execution, () -> initiatePayment(context,
                params.getPath(),
                params.getHeaders(),
                params.getBody()));
    }

    @Override
    public boolean isXs2aApiVersionSupported(String apiVersion) {
        return Strings.isBlank(apiVersion);
    }

    private Response<PaymentInitationRequestResponse201> initiatePayment(
            Xs2aPisContext context,
            Xs2aInitialPaymentParameters path,
            PaymentInitiateHeaders headers,
            PaymentInitiationJson body) {

        logResolver.log("initiatePayment with parameters: {}", path, headers, body);

        Response<PaymentInitationRequestResponse201> paymentInit = pis.initiatePayment(PaymentService.PAYMENTS,
                path.getPaymentProduct(),
                QuirkUtil.pushBicToXs2aAdapterHeaders(context, headers.toHeaders()),
                RequestParams.empty(),
                body
        );

        logResolver.log("initiatePayment response: {}", paymentInit);
        return paymentInit;
    }

    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
            Xs2aInitialPaymentParameters,
            PaymentInitiateHeaders,
            PaymentInitiateBody,
            PaymentInitiationJson> {

        public Extractor(
                DtoMapper<Xs2aPisContext, PaymentInitiateBody> toValidatableBody,
                DtoMapper<PaymentInitiateBody, PaymentInitiationJson> toBody,
                DtoMapper<Xs2aPisContext, PaymentInitiateHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aInitialPaymentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }

    }
}
