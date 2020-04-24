package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Transactions that occurred on account object.
 */
@Value
@Builder
public class AccountReport {

  /**
   * Transactions that were already processed.
   */
  private TransactionListBody booked;

  /**
   * Transactions that are to be processed.
   */
  private TransactionListBody pending;
}
