package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Transaction details representation list transactions result from protocol.
 */
@Value
@Builder
public class TransactionDetailsBody {
  private String transactionId;
  private String entryReference;
  private String endToEndId;
  private String mandateId;
  private String checkId;
  private String creditorId;
  private LocalDate bookingDate;
  private LocalDate valueDate;
  private Amount transactionAmount;
  private String creditorName;
  private AccountReference creditorAccount;
  private String ultimateCreditor;
  private String debtorName;
  private AccountReference debtorAccount;
  private String ultimateDebtor;
  private String remittanceInformationUnstructured;
  private String remittanceInformationStructured;
  private String proprietaryBankTransactionCode;
 }
