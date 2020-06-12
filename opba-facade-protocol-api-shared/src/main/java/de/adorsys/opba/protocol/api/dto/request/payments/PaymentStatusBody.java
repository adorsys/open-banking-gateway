package de.adorsys.opba.protocol.api.dto.request.payments;

import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import lombok.Data;

/**
 * Pis payment status body
 */
@Data
public class PaymentStatusBody implements ResultBody {
    private String transactionStatus;
}
