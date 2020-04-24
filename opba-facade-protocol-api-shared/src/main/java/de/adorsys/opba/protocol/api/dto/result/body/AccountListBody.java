package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Account list result from protocol.
 */
@Value
@Builder
public class AccountListBody implements ResultBody {
    List<AccountListDetailBody> accounts;

    @Override
    public Object getBody() {
        return this;
    }
}
