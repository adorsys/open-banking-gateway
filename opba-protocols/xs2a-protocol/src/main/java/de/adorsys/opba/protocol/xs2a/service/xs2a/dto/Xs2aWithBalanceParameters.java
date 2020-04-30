package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestParams;
import lombok.Data;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * XS2A account withBalance describing parameters.
 */
@Data
public class Xs2aWithBalanceParameters {

    // Optional
    /**
     * Does consent require account balance access.
     */
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
