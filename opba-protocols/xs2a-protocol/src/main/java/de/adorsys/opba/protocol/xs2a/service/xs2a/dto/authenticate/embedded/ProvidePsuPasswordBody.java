package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.PSU_PASSWORD;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;

/**
 * Mapper from {@link Xs2aContext} to {@link UpdatePsuAuthentication} object to pass password/TAN to XS2A adapter.
 */
@Data
public class ProvidePsuPasswordBody {

    /**
     * PSU PIN/password to call XS2A api with.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(PSU_PASSWORD))
    @NotBlank(message = "{no.psu.password}")
    private String psuPassword;

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> {

        @Mapping(target = "psuData.password", source = "psuPassword")
        UpdatePsuAuthentication map(ProvidePsuPasswordBody cons);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, ProvidePsuPasswordBody> {
        ProvidePsuPasswordBody map(Xs2aContext ctx);
    }
}
