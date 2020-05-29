package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.pis.GetPaymentInformationService;
import de.adorsys.opba.protocol.facade.services.pis.GetPaymentStatusService;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInitiationWithStatusResponse;
import de.adorsys.opba.tppbankingapi.pis.resource.generated.TppBankingApiPaymentStatusPisApi;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class TppBankingApiPisInfoController implements TppBankingApiPaymentStatusPisApi {
    private final GetPaymentStatusService paymentStatusService;
    private final GetPaymentInformationService paymentInfoService;
    private final UserAgentContext userAgentContext;
    private final FacadeResponseMapper mapper;
    private final PaymentInfoBodyToApiMapper paymentInfoResponseMapper;
    private final PaymentStatusBodyToApiMapper paymentStatusResponseMapper;


    @Override
    public CompletableFuture getPaymentInformation(UUID xRequestID) {
        return paymentInfoService.execute(
                PaymentInfoRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .build()
                        )
                        .build()
        ).thenApply((FacadeResult<PaymentInfoBody> result) -> mapper.translate(result, paymentInfoResponseMapper));
    }

    @Override
    public CompletableFuture getPaymentInitiationStatus(UUID xRequestID) {
        return paymentStatusService.execute(
                PaymentStatusRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .build()
                        )
                        .build()
        ).thenApply((FacadeResult<PaymentStatusBody> result) -> mapper.translate(result, paymentStatusResponseMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface PaymentInfoBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInitiationWithStatusResponse, PaymentInfoBody> {
        PaymentInitiationWithStatusResponse map(PaymentInfoBody facade);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface PaymentStatusBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInitiationWithStatusResponse, PaymentStatusBody> {
        PaymentInitiationWithStatusResponse map(PaymentStatusBody facade);
    }
}
