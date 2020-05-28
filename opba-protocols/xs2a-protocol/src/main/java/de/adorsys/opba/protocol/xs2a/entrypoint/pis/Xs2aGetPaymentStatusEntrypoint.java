package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.api.pis.GetPaymentStatusState;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.PaymentInitiationStatus;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Entry point to get payment status. BPMN engine and process is not touched.
 */
@Service("xs2aGetPaymentStatusState")
@RequiredArgsConstructor
public class Xs2aGetPaymentStatusEntrypoint implements GetPaymentStatusState {
    private final PaymentInitiationService pis;
    private final PaymentStatusToBodyMapper mapper;

    @Override
    public CompletableFuture<Result<PaymentStatusBody>> execute(ServiceContext<PaymentStatusRequest> serviceContext) {
        ProtocolFacingConsent consent = serviceContext.getRequestScoped().consentAccess().findByCurrentServiceSession()
                .orElseThrow(() -> new IllegalStateException("Context not found"));

        Response<PaymentInitiationStatus> paymentStatus = pis.getSinglePaymentInitiationStatus(
                "sepa-credit-transfers",
                consent.getConsentId(),
                RequestHeaders.fromMap(new HashMap<>()),
                RequestParams.fromMap(new HashMap<>()));

        Result<PaymentStatusBody> result = new SuccessResult<>(mapper.map(paymentStatus.getBody()));
        return CompletableFuture.completedFuture(result);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface PaymentStatusToBodyMapper {
        PaymentStatusBody map(PaymentInitiationStatus paymentInformation);
    }
}
