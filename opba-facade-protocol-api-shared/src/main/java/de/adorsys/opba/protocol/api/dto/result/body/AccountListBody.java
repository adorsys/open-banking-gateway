package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Builder
@Data
@Slf4j
public class AccountListBody implements ResultBody {
    private List<AccountListDetailBody> accounts;

    @Override
    public Object getBody() {
        return Mappers.getMapper(FacadeToProtocolMapper.class).mapFromFacadeToProtocol(this);
    }
}
