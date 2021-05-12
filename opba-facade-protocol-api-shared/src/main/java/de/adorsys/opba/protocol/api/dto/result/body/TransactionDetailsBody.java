package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Transaction details representation list transactions result from protocol.
 */
@Data
@Builder
public class TransactionDetailsBody {

    String transactionId;
    String entryReference;
    String endToEndId;
    String mandateId;
    String checkId;
    String creditorId;
    LocalDate bookingDate;
    LocalDate valueDate;
    Amount transactionAmount;
    String creditorName;
    AccountReference creditorAccount;
    String ultimateCreditor;
    String debtorName;
    AccountReference debtorAccount;
    String ultimateDebtor;
    String remittanceInformationUnstructured;
    String remittanceInformationStructured;
    String proprietaryBankTransactionCode;
}
