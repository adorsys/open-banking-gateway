package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.pis.SinglePaymentService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInitiation;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInitiationResponse;
import de.adorsys.opba.tppbankingapi.pis.resource.generated.TppBankingApiSinglePaymentPisApi;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.tppbankingapi.Const.API_MAPPERS_PACKAGE;

@RestController
@RequiredArgsConstructor
public class TppBankingApiPisController implements TppBankingApiSinglePaymentPisApi {
    private final UserAgentContext userAgentContext;
    private final FacadeResponseMapper mapper;
    private final SinglePaymentService payments;
    private final PaymentRestRequestBodyToSinglePaymentMapper pisSinglePaymentMapper;
    private final PaymentFacadeResponseBodyToRestBodyMapper paymentResponseMapper;

    @Override
    public CompletableFuture initiatePayment(PaymentInitiation body,
                                             String serviceSessionPassword,
                                             String fintechUserID,
                                             String fintechRedirectURLOK,
                                             String fintechRedirectURLNOK,
                                             UUID xRequestID,
                                             String paymentProduct,
                                             String xTimestampUTC,
                                             String xRequestSignature,
                                             String fintechID,
                                             UUID bankProfileID,
                                             Boolean xPsuAuthenticationRequired,
                                             Boolean computePsuIpAddress,
                                             String psuIpAddress
    ) {
        return payments.execute(
                InitiateSinglePaymentRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                                   // Get rid of CGILIB here by copying:
                                                   .uaContext(userAgentContext.toBuilder().build())
                                                   .authorization(fintechID)
                                                   .sessionPassword(serviceSessionPassword)
                                                   .fintechUserId(fintechUserID)
                                                   .fintechRedirectUrlOk(fintechRedirectURLOK)
                                                   .fintechRedirectUrlNok(fintechRedirectURLNOK)
                                                   .requestId(xRequestID)
                                                   .bankProfileId(bankProfileID)
                                                   .anonymousPsu(null != xPsuAuthenticationRequired && !xPsuAuthenticationRequired)
                                                   .build()
                        )
                        .singlePayment(pisSinglePaymentMapper.map(body, PaymentProductDetails.fromValue(paymentProduct)))
                        .build()
        ).thenApply((FacadeResult<SinglePaymentBody> result) -> mapper.translate(result, paymentResponseMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentRestRequestBodyToSinglePaymentMapper {
        @Mapping(source = "body.creditorAddress.townName", target = "creditorAddress.city")
        SinglePaymentBody map(PaymentInitiation body, PaymentProductDetails paymentProduct);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInitiationResponse, SinglePaymentBody> {
        PaymentInitiationResponse map(SinglePaymentBody facadeEntity);
    }
}
