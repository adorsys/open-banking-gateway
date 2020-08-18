package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.pis.GetPaymentInformationService;
import de.adorsys.opba.protocol.facade.services.pis.GetPaymentStatusService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInformationResponse;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentStatusResponse;
import de.adorsys.opba.tppbankingapi.pis.resource.generated.TppBankingApiPaymentStatusPisApi;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class TppBankingApiPisInfoController implements TppBankingApiPaymentStatusPisApi {
    private final GetPaymentStatusService paymentStatusService;
    private final GetPaymentInformationService paymentInfoService;
    private final FacadeResponseMapper mapper;
    private final PaymentInfoBodyToApiMapper paymentInfoResponseMapper;
    private final PaymentStatusBodyToApiMapper paymentStatusResponseMapper;


    @Override
    public CompletableFuture getPaymentInformation(String serviceSessionPassword,
                                                   String fintechUserID,
                                                   UUID xRequestID,
                                                   String paymentProduct,
                                                   String xTimestampUTC,
                                                   String xRequestSignature,
                                                   String fintechID,
                                                   String bankID,
                                                   UUID serviceSessionID) {
        return paymentInfoService.execute(
                PaymentInfoRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                .requestId(xRequestID)
                                .bankId(bankID)
                                .sessionPassword(serviceSessionPassword)
                                .fintechUserId(fintechUserID)
                                .authorization(fintechID)
                                .serviceSessionId(serviceSessionID)
                                .anonymousPsuAllowed(true)
                                .build()
                        )
                        .paymentProduct(PaymentProductDetails.fromValue(paymentProduct))
                        .build()
        ).thenApply((FacadeResult<PaymentInfoBody> result) -> mapper.translate(result, paymentInfoResponseMapper));
    }

    @Override
    public CompletableFuture getPaymentStatus(String serviceSessionPassword,
                                                        String fintechUserID,
                                                        UUID xRequestID,
                                                        String paymentProduct,
                                                        String xTimestampUTC,
                                                        String xRequestSignature,
                                                        String fintechID,
                                                        String bankID,
                                                        UUID serviceSessionID) {
        return paymentStatusService.execute(
                PaymentStatusRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                .requestId(xRequestID)
                                .bankId(bankID)
                                .sessionPassword(serviceSessionPassword)
                                .fintechUserId(fintechUserID)
                                .authorization(fintechID)
                                .serviceSessionId(serviceSessionID)
                                .anonymousPsuAllowed(true)
                                .build()
                        )
                        .paymentProduct(PaymentProductDetails.fromValue(paymentProduct))
                        .build()
        ).thenApply((FacadeResult<PaymentStatusBody> result) -> mapper.translate(result, paymentStatusResponseMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface PaymentInfoBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInformationResponse, PaymentInfoBody> {

        @Mapping(source = "facade.creditorAddress.city", target = "creditorAddress.townName")
        PaymentInformationResponse map(PaymentInfoBody facade);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface PaymentStatusBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<PaymentStatusResponse, PaymentStatusBody> {
        PaymentStatusResponse map(PaymentStatusBody facade);
    }
}
