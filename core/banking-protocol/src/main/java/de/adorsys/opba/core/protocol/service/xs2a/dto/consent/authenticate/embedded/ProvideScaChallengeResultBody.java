package de.adorsys.opba.core.protocol.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class ProvideScaChallengeResultBody {

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("lastScaChallenge"))
    @NotBlank(message = "{no.sca.challenge.result}")
    private String scaChallenge;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<ProvideScaChallengeResultBody, TransactionAuthorisation> {

        @Mapping(target = "scaAuthenticationData", source = "scaChallenge")
        TransactionAuthorisation map(ProvideScaChallengeResultBody cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, ProvideScaChallengeResultBody> {
        @Mapping(target = "scaChallenge", source = "lastScaChallenge")
        ProvideScaChallengeResultBody map(Xs2aContext ctx);
    }
}
