package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Builder
@Data
@Slf4j
public class AccountListBody implements ResultBody {
    private List<AccountListDetailBody> accountListDetails;

    @Override
    public Object getBody() {
        return new FacadeToProtocolMapper().getProtocolEntity(this);
    }
}
