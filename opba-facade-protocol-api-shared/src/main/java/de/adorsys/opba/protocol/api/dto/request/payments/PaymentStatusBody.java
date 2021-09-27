package de.adorsys.opba.protocol.api.dto.request.payments;

import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Pis payment status body
 */
@Data
public class PaymentStatusBody implements ResultBody {
    private String externalResourceId;
    private String transactionStatus;
    private OffsetDateTime createdAt;
}
