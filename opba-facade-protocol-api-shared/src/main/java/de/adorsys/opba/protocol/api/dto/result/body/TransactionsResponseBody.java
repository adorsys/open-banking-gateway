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
  AccountReference account;

  /**
   * List of the transactions.
   */
  AccountReport transactions;

  /**
   * Transaction categorization result, optional.
   */
  AnalyticsResult analytics;

  /**
   * Optional information for pagination
   */
  Paging paging;

  @Override
  public Object getBody() {
    return this;
  }
}
