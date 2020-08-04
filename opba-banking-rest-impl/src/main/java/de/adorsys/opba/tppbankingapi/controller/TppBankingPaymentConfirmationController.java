package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.service.PaymentConfirmationService;
import de.adorsys.opba.tppbankingapi.token.model.generated.PsuPaymentSessionResponse;
import de.adorsys.opba.tppbankingapi.token.resource.generated.PaymentConfirmationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;

@RestController
@RequiredArgsConstructor
public class TppBankingPaymentConfirmationController implements PaymentConfirmationApi {

    private final PaymentConfirmationService consentConfirmationService;

    @Override
    public ResponseEntity<PsuPaymentSessionResponse> confirmPayment(String authId,
                                                                    UUID xRequestID,
                                                                    String serviceSessionPassword,
                                                                    String xTimestampUTC,
                                                                    String xRequestSignature,
                                                                    String fintechID) {
        UUID authorizationSessionId = UUID.fromString(authId);

        if (!consentConfirmationService.confirmPayment(authorizationSessionId, serviceSessionPassword)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PsuPaymentSessionResponse response = new PsuPaymentSessionResponse();
        response.setAuthorizationSessionId(authorizationSessionId);

        return ResponseEntity.ok()
                .header(X_REQUEST_ID, xRequestID.toString())
                .body(response);
    }
}
