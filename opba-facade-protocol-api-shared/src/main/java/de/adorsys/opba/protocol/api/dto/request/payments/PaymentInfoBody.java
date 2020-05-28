package de.adorsys.opba.protocol.api.dto.request.payments;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.Address;
import de.adorsys.opba.protocol.api.dto.result.body.Amount;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import lombok.Data;

/**
 * Pis payment information body
 */
@Data
public class PaymentInfoBody implements ResultBody {
    private String endToEndIdentification;
    private AccountReference debtorAccount;
    private Amount instructedAmount;
    private AccountReference creditorAccount;
    private String creditorAgent;
    private String creditorName;
    private Address creditorAddress;
    private String remittanceInformationUnstructured;
    private String transactionStatus;
}
