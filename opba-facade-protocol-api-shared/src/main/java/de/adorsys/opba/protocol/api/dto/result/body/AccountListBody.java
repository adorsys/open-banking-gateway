package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Data;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Builder
@Data
public class AccountListBody implements ResultBody {
    private List<AccountListDetailBody> accounts;

    @Override
    public Object getBody() {
        return this;
//        return Mappers.getMapper(FacadeToProtocolMapper.class).mapFromFacadeToProtocol(this);
    }
}
