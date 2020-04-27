package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethod;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.SCA_METHOD_ID;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;

/**
 * Mapper from {@link Xs2aContext} to {@link SelectPsuAuthenticationMethod} object to pass what kind of SCA method
 * user wants to use to XS2A-adapter API.
 */
@Data
public class SelectScaChallengeBody {

    /**
     * PSU SCA method selected id (id of SMS or id of EMAIL etc. SCA method).
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(SCA_METHOD_ID))
    @NotBlank(message = "{no.sca.challenge.method.selected}")
    private String authenticationMethodId;

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<SelectScaChallengeBody, SelectPsuAuthenticationMethod> {
        SelectPsuAuthenticationMethod map(SelectScaChallengeBody cons);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, SelectScaChallengeBody> {

        @Mapping(target = "authenticationMethodId", source = "userSelectScaId")
        SelectScaChallengeBody map(Xs2aContext ctx);
    }
}
