package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Initial parameters (after consent was initiated) to be supplied to XS2A-adapter.
 */
@Data
public class Xs2aInitialConsentParameters {

    /**
     * Consent ID that is used for current operation. (Authorized for)
     */
    @NotBlank // can't be provided manually
    private String consentId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aInitialConsentParameters> {
        Xs2aInitialConsentParameters map(Xs2aContext ctx);
    }
}
