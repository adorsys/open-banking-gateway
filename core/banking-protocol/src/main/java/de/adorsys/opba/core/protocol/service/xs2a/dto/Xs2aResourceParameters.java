package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aResourceParameters {

    @NotBlank
    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("resourceId"))
    @NotNull(message = "{no.ctx.resourceId}")
    private String resourceId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aResourceParameters> {
        Xs2aResourceParameters map(Xs2aContext ctx);
    }
}
