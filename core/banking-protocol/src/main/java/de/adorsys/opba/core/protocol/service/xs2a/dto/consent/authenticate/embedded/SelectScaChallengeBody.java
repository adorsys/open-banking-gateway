package de.adorsys.opba.core.protocol.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethod;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class SelectScaChallengeBody {

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("userSelectScaId"))
    @NotBlank(message = "{no.sca.challenge.method.selected}")
    private String authenticationMethodId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<SelectScaChallengeBody, SelectPsuAuthenticationMethod> {
        SelectPsuAuthenticationMethod map(SelectScaChallengeBody cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, SelectScaChallengeBody> {

        @Mapping(target = "authenticationMethodId", source = "userSelectScaId")
        SelectScaChallengeBody map(Xs2aContext ctx);
    }
}
