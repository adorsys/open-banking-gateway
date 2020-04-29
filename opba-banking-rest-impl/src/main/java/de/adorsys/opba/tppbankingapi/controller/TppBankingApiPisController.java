package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.pis.SinglePaymentService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInitiation;
import de.adorsys.opba.tppbankingapi.pis.model.generated.PaymentInitiationResponse;
import de.adorsys.xs2a.adapter.service.psd2.model.PaymentInitiation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.tppbankingapi.Const.API_MAPPERS_PACKAGE;

@RestController
@RequiredArgsConstructor
//public class TppBankingApiPisController implements TppBankingApiPaymentInitiationServicePisApi {
public class TppBankingApiPisController {
    private final UserAgentContext userAgentContext;
    private final FacadeResponseMapper mapper;
    private final SinglePaymentService payments;
    private final PaymentRestRequestBodyToSinglePaymentMapper pisSinglePaymentMapper;
    private final PaymentFacadeResponseBodyToRestBodyMapper paymentResponseMapper;

    @RequestMapping(value = "/v1/banking/pis/payments/{payment-product}",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity> _initiatePayment(@ApiParam(value = "", required = true) @Valid @RequestBody PaymentInitiation body
            , @ApiParam(value = "Password to encrypt users' context ", required = true) @RequestHeader(value = "Service-Session-Password", required = true) String serviceSessionPassword
            ,
                                                                                         @ApiParam(value = "An End-User identifier, known by FinTech, that unique for each physical person. To be provided by FinTech with every request in order to validate the mapping of service request to the particular user. ", required = true)
                                                                                         @RequestHeader(value = "Fintech-User-ID", required = true) String fintechUserID
            , @ApiParam(value = "", required = true) @RequestHeader(value = "Fintech-Redirect-URL-OK", required = true) String fintechRedirectURLOK
            , @ApiParam(value = "", required = true) @RequestHeader(value = "Fintech-Redirect-URL-NOK", required = true) String fintechRedirectURLNOK
            , @ApiParam(value = "Unique ID that identifies this request through common workflow. Shall be contained in HTTP Response as well. ", required = true)
                                                                                         @RequestHeader(value = "X-Request-ID", required = true) UUID xRequestID
            ,
                                                                                         @ApiParam(value = "", required = true, allowableValues = "sepa-credit-transfers, instant-sepa-credit-transfers, target-2-payments, cross-border-credit-transfers, pain.001-sepa-credit-transfers, pain.001-instant-sepa-credit-transfers, pain.001-target-2-payments, pain.001-cross-border-credit-transfers")
                                                                                         @PathVariable("payment-product") String paymentProduct
            , @ApiParam(value = "The timestamp of the operation. ") @RequestHeader(value = "X-Timestamp-UTC", required = false) String xTimestampUTC
            , @ApiParam(value = "A signature of the request by the TPP fintech. ") @RequestHeader(value = "X-Request-Signature", required = false) String xRequestSignature
            , @ApiParam(value = "Unique ID that identifies fintech. ") @RequestHeader(value = "Fintech-ID", required = false) String fintechID
            , @ApiParam(value = "A bank identifier, provided by TPP Bank Search API. To be provided by FinTech only if PsuConsentSession is missing. ") @RequestHeader(value = "Bank-ID", required = false) String bankID
            ,@ApiParam(value = "Unique Token that identifies PSU Consent Session for this request, if it is already available for given PSU by Fintech through previous requests. This Token is not designed to be parsed at Fintech side. " ) @RequestHeader(value="PSU-Consent-Session", required=false) String psUConsentSession
            , @ApiParam(value = "Unique ID that identifies service session. Can be used for batch processing to correlate input and output. ") @RequestHeader(value = "Service-Session-ID", required = false) UUID serviceSessionID
    ) {
        return initiatePayment(body, serviceSessionPassword, fintechUserID, fintechRedirectURLOK, fintechRedirectURLNOK, xRequestID, paymentProduct, xTimestampUTC, xRequestSignature, fintechID,
                               bankID, psUConsentSession, serviceSessionID);
    }

    //@Override
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
                                             String bankID,
                                             String psUConsentSession,
                                             UUID serviceSessionID
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
                                                   .serviceSessionId(serviceSessionID)
                                                   .requestId(xRequestID)
                                                   .bankId(bankID)
                                                   .build()
                        )
                        .singlePayment(pisSinglePaymentMapper.map(body))
                        .build()
        ).thenApply((FacadeResult<SinglePaymentBody> result) -> mapper.translate(result, paymentResponseMapper));
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentRestRequestBodyToSinglePaymentMapper {
        SinglePaymentBody map(PaymentInitiation body);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInitiationResponse, SinglePaymentBody> {
        PaymentInitiationResponse map(SinglePaymentBody facadeEntity);
    }
}
