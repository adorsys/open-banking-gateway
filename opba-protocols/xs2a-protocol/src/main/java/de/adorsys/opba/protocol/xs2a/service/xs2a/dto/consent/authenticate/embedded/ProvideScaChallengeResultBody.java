package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.SCA_CHALLENGE_RESULT;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;

@Data
public class ProvideScaChallengeResultBody {

    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(SCA_CHALLENGE_RESULT))
    @NotBlank(message = "{no.sca.challenge.result}")
    private String scaChallenge;

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<ProvideScaChallengeResultBody, TransactionAuthorisation> {

        @Mapping(target = "scaAuthenticationData", source = "scaChallenge")
        TransactionAuthorisation map(ProvideScaChallengeResultBody cons);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, ProvideScaChallengeResultBody> {
        @Mapping(target = "scaChallenge", source = "lastScaChallenge")
        ProvideScaChallengeResultBody map(Xs2aContext ctx);
    }
}
