package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aInitialConsentParameters {

    @NotBlank // can't be provided manually
    private String consentId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aInitialConsentParameters> {
        Xs2aInitialConsentParameters map(Xs2aContext ctx);
    }
}
