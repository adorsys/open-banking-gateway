package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.PaymentInitiationWithStatusResponse;
import de.adorsys.opba.fintech.api.resource.generated.FintechRetrieveAllSinglePaymentsApi;
import de.adorsys.opba.fintech.impl.service.PaymentService;
import de.adorsys.opba.fintech.impl.service.SessionLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FintechRetrieveAllSinglePaymentsImpl implements FintechRetrieveAllSinglePaymentsApi {
    private final PaymentService paymentService;
    private final SessionLogicService sessionLogicService;

    @Override
    public ResponseEntity<List<PaymentInitiationWithStatusResponse>> retrieveAllSinglePayments(
            String bankId,
            String accountId,
            UUID xRequestID,
            String xsrfToken
    ) {
        log.debug("got list all payment requrest");

        if (!sessionLogicService.isSessionAuthorized()) {
            log.warn("list all payments failed: user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return sessionLogicService.addSessionMaxAgeToHeader(
                paymentService.retrieveAllSinglePayments(bankId, accountId)
        );
    }
}
