package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestParams;
import lombok.Data;
import org.mapstruct.Mapper;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aWithBalanceParameters {

    // Optional
    private Boolean withBalance;

    // TODO - MapStruct?
    public RequestParams toParameters() {
        return RequestParams.builder().withBalance(withBalance).build();
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aWithBalanceParameters> {
        Xs2aWithBalanceParameters map(Xs2aContext ctx);
    }
}
