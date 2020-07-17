package de.adorsys.opba.api.security.external.domain.signdata;

import de.adorsys.opba.api.security.external.domain.OperationType;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * The header values in http request from fintech to opba, that take part in signature verification.
 * The 'X-Request-Signature' header contains signed representation of each the following field.
 * This values are signed on the side of fintech and are verified in a spring filter on the side of opba.
 */
@Value
public class ConfirmPaymentDataToSign {
    UUID xRequestId;
    Instant instant;
    OperationType operationType;
}
