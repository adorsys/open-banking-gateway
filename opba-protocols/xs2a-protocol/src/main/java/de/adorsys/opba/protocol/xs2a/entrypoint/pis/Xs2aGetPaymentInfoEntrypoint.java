package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.api.pis.GetPaymentInfoState;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.Xs2aUuidMapper;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInfoHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInfoParameters;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.SinglePaymentInitiationInformationWithStatusResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Entry point to get payment information. BPMN engine and process is not touched.
 */
@Service("xs2aGetPaymentInfoState")
@RequiredArgsConstructor
public class Xs2aGetPaymentInfoEntrypoint implements GetPaymentInfoState {
    private final PaymentInitiationService pis;
    private final PaymentInformationToBodyMapper mapper;
    private final Xs2aGetPaymentInfoEntrypoint.Extractor extractor;
    private final Xs2aGetPaymentInfoEntrypoint.FromRequest request2ContextMapper;
    private final ExtendWithServiceContext extender;

    @Override
    @Transactional
    public CompletableFuture<Result<PaymentInfoBody>> execute(ServiceContext<PaymentInfoRequest> context) {
        ProtocolFacingConsent consent = context.getRequestScoped().consentAccess().getByCurrentSession();

        ValidatedPathHeaders<PaymentInfoParameters, PaymentInfoHeaders> params = extractor.forExecution(prepareContext(context));

        Response<SinglePaymentInitiationInformationWithStatusResponse> paymentInformation = pis.getSinglePaymentInformation(
                context.getRequest().getPaymentProduct().toString(),
                consent.getConsentId(),
                params.getHeaders().toHeaders(),
                params.getPath().toParameters()
        );

        Result<PaymentInfoBody> result = new SuccessResult<>(mapper.map(paymentInformation.getBody()));
        return CompletableFuture.completedFuture(result);
    }

    protected Xs2aPisContext prepareContext(ServiceContext<PaymentInfoRequest> serviceContext) {
        Xs2aPisContext context = request2ContextMapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.GET_PAYMENT_INFORMATION);
        extender.extend(context, serviceContext);
        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = Xs2aUuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<PaymentInfoRequest, Xs2aPisContext> {

        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        Xs2aPisContext map(PaymentInfoRequest ctx);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface PaymentInformationToBodyMapper {
        PaymentInfoBody map(SinglePaymentInitiationInformationWithStatusResponse paymentInformation);
    }

    @Service
    public static class Extractor extends PathHeadersMapperTemplate<
                Xs2aPisContext,
                PaymentInfoParameters,
                PaymentInfoHeaders> {

        public Extractor(
                DtoMapper<Xs2aPisContext, PaymentInfoHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, PaymentInfoParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }
}
