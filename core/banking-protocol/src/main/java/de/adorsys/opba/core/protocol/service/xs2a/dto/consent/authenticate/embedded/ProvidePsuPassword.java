package de.adorsys.opba.core.protocol.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class ProvidePsuPassword {

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("psuPassword"))
    @NotBlank(message = "{no.psu.password}")
    private String psuPassword;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi {

        @Mapping(target = "psuData.password", source = "psuPassword")
        UpdatePsuAuthentication map(ProvidePsuPassword cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx {
        ProvidePsuPassword map(Xs2aContext ctx);
    }
}
