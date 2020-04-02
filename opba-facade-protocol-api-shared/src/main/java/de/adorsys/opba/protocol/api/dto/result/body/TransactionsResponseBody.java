package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionsResponseBody implements ResultBody {
  private AccountReference account;
  private AccountReport transactions;

  @Override
  public Object getBody() {
    return this;
  }
}
