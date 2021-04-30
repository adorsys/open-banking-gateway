package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import de.adorsys.xs2a.adapter.api.model.AccountReference;
import de.adorsys.xs2a.adapter.api.model.Address;
import de.adorsys.xs2a.adapter.api.model.Amount;
import lombok.Data;


@Data
public class PaymentInitiationJsonLog implements NotSensitiveData {

    private String endToEndIdentification;
    private AccountReference debtorAccount;
    private Amount instructedAmount;
    private AccountReference creditorAccount;
    private String creditorAgent;
    private String creditorAgentName;
    private String creditorName;
    private Address creditorAddress;
    private String remittanceInformationUnstructured;

    @Override
    public String getNotSensitiveData() {
        return "PaymentInitiationJsonLog("
                + ")";
    }
}
