package de.adorsys.opba.protocol.hbci.service.protocol.pis.dto;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.Address;
import de.adorsys.opba.protocol.api.dto.result.body.Amount;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentInitiateBody {
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
}
