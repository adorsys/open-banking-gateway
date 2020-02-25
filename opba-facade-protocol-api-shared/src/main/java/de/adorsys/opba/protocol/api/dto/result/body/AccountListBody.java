package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AccountListBody implements ResultBody {
    private List<AccountListDetailBody> accounts;

    @Override
    public Object getBody() {
        return this;
    }
}
