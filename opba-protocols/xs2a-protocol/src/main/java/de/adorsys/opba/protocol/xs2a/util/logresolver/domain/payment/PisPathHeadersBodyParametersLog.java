package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import de.adorsys.xs2a.adapter.api.model.AccountReference;
import de.adorsys.xs2a.adapter.api.model.Address;
import de.adorsys.xs2a.adapter.api.model.Amount;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import lombok.Data;


@Data
public class PisPathHeadersBodyParametersLog {

    private PaymentType paymentType;
    private PaymentProduct paymentProduct;

    private String psuId;
    private String aspspId;
    private String requestId;
    private String oauth2Token;
    private String psuIpAddress;
    private String redirectUriOk;
    private String redirectUriNok;
    private String psuIpPort;

    private String endToEndIdentification;
    private AccountReference debtorAccount;
    private Amount instructedAmount;
    private AccountReference creditorAccount;
    private String creditorAgent;
    private String creditorAgentName;
    private String creditorName;
    private Address creditorAddress;
    private String remittanceInformationUnstructured;

    public String getNotSensitiveData() {
        return "PathHeadersBodyParametersLog("
                + ", requestId=" + this.getRequestId()
                + ")";
    }
}
