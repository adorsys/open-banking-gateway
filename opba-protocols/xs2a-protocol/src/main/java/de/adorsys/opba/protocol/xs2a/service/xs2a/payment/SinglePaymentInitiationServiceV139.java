package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import com.vdurmont.semver4j.Semver;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentOrPaymentPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateV139Headers;
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
public class SinglePaymentInitiationServiceV139 implements SinglePaymentInitiationService {

    private static final Semver XS2A_API_VERSION = new Semver("1.3.9");

    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final ExtractorV139 extractorV139;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    public void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractorV139.forValidation(context));
    }

    @Override
    public Response<PaymentInitationRequestResponse201> doExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        var params = extractorV139.forExecution(context);
        return handler.tryCreateAndHandleErrors(execution, () -> initiatePayment(context,
                params.getPath(),
                params.getHeaders(),
                params.getBody()));

    }

    @Override
    public boolean isXs2aApiVersionSupported(String apiVersion) {

     return Strings.isNotBlank(apiVersion) && !VERSION_DIFFS.contains(XS2A_API_VERSION.diff(apiVersion));
    }

    private Response<PaymentInitationRequestResponse201> initiatePayment(
            Xs2aPisContext context,
            Xs2aInitialPaymentParameters path,
            PaymentInitiateV139Headers headers,
            PaymentInitiationJson body) {

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
    public static class ExtractorV139 extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
            Xs2aInitialPaymentParameters,
            PaymentInitiateV139Headers,
            PaymentInitiateBody,
            PaymentInitiationJson> {

        public ExtractorV139(
                DtoMapper<Xs2aPisContext, PaymentInitiateBody> toValidatableBody,
                DtoMapper<PaymentInitiateBody, PaymentInitiationJson> toBody,
                DtoMapper<Xs2aPisContext, PaymentInitiateV139Headers> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aInitialPaymentParameters> toParameters) {

            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
