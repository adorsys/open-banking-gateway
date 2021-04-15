package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Transaction result list transactions result from protocol.
 */
@Value
@Builder
public class TransactionsResponseBody implements ResultBody {

  /**
   * Account on which the transactions happened.
   */
  private AccountReference account;

  /**
   * List of the transactions.
   */
  private AccountReport transactions;

  /**
   * Transaction categorization result, optional.
   */
  private AnalyticsResult analytics;

  @Override
  public Object getBody() {
    return this;
  }
}
