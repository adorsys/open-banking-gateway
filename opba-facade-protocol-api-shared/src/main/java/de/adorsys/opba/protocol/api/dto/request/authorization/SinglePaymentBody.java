package de.adorsys.opba.protocol.api.dto.request.authorization;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.Address;
import de.adorsys.opba.protocol.api.dto.result.body.Amount;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import lombok.Data;

import java.time.LocalDate;

/**
 * Pis Single payment
 */
@Data
public class SinglePaymentBody implements ResultBody {
    private String paymentId;

    private AccountReference creditorAccount;
    private Address creditorAddress;
    private String creditorAgent;
    private String creditorName;

    private AccountReference debtorAccount;
    private String endToEndIdentification;
    private Amount instructedAmount;

    private PaymentProductDetails paymentProduct;
    private String paymentStatus;

    // optional
    private String remittanceInformationUnstructured;
    private LocalDate requestedExecutionDate;
    private String requestedExecutionTime;

    @Override
    public Object getBody() {
        return this;
    }
}
