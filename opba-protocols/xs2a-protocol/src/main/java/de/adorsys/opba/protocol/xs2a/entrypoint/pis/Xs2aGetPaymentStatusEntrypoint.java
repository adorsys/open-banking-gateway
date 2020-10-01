package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.api.pis.GetPaymentStatusState;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.Xs2aUuidMapper;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentStateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentStateParameters;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.PaymentInitiationStatus;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Entry point to get payment status. BPMN engine and process is not touched.
 */
@Service("xs2aGetPaymentStatusState")
@RequiredArgsConstructor
public class Xs2aGetPaymentStatusEntrypoint implements GetPaymentStatusState {

    private final Xs2aPaymentContextLoader contextLoader;
    private final PaymentInitiationService pis;
    private final PaymentStatusToBodyMapper mapper;
    private final Xs2aGetPaymentStatusEntrypoint.Extractor extractor;
    private final Xs2aGetPaymentStatusEntrypoint.FromRequest request2ContextMapper;
    private final ExtendWithServiceContext extender;

    @Override
    @Transactional
    public CompletableFuture<Result<PaymentStatusBody>> execute(ServiceContext<PaymentStatusRequest> context) {
        ProtocolFacingPayment payment = context.getRequestScoped().paymentAccess().getFirstByCurrentSession();

        ValidatedPathHeaders<PaymentStateParameters, PaymentStateHeaders> params = extractor.forExecution(prepareContext(context, payment));

        Response<PaymentInitiationStatus> paymentStatus = pis.getSinglePaymentInitiationStatus(
                context.getRequest().getPaymentProduct().toString(),
                payment.getPaymentId(),
                params.getHeaders().toHeaders(),
                params.getPath().toParameters()
        );

        // All we can do to return guaranteed payment date is read the payment date directly from our payment entity
        // As NextGenPSD2 1.3.6 does not guarantee any payment date/time fields at this endpoint
        Result<PaymentStatusBody> result = new SuccessResult<>(mapper.map(paymentStatus.getBody()));
        result.getBody().setCreatedAt(payment.getCreatedAtTime().atOffset(ZoneOffset.UTC));
        return CompletableFuture.completedFuture(result);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface PaymentStatusToBodyMapper {
        PaymentStatusBody map(PaymentInitiationStatus paymentStatus);
    }


    protected Xs2aPisContext prepareContext(ServiceContext<PaymentStatusRequest> serviceContext, ProtocolFacingPayment payment) {
        Xs2aPisContext context = request2ContextMapper.map(serviceContext.getRequest());
        Xs2aPisContext paymentInitiationContext = contextLoader.loadContext(payment);
        context.setOauth2Token(paymentInitiationContext.getOauth2Token());
        context.setAction(ProtocolAction.GET_PAYMENT_STATUS);
        extender.extend(context, serviceContext);
        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = Xs2aUuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<PaymentStatusRequest, Xs2aPisContext> {

        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        Xs2aPisContext map(PaymentStatusRequest ctx);
    }

    @Service
    public static class Extractor extends PathHeadersMapperTemplate<
                Xs2aPisContext,
                PaymentStateParameters,
                PaymentStateHeaders> {

        public Extractor(
                DtoMapper<Xs2aPisContext, PaymentStateHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, PaymentStateParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }
}
