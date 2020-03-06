package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aResourceParameters {

    @NotBlank(message = "{no.ctx.resourceId}")
    @NotNull(message = "{no.ctx.resourceId}")
    private String resourceId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<TransactionListXs2aContext, Xs2aResourceParameters> {
        Xs2aResourceParameters map(TransactionListXs2aContext ctx);
    }
}
