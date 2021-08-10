package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Transaction result list transactions result from protocol.
 */
@Value
@Builder(toBuilder = true)
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

  /**
   * Optional information for pagination
   */
  private Paging paging;

  @Override
  public Object getBody() {
    return this;
  }
}
