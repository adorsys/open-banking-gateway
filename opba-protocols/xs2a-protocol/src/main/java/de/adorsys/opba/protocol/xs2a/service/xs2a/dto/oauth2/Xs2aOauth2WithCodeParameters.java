package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.Oauth2Service;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aOauth2WithCodeParameters {

    @NotBlank
    private String oauth2RedirectBackLink;

    @NotBlank
    private String oauth2Code;

    @NotBlank
    private String grantType = Oauth2Service.GrantType.AUTHORIZATION_CODE.toString(); // TODO Xs2a Adapter should set it?

    public Oauth2Service.Parameters toParameters() {
        Oauth2Service.Parameters parameters = new Oauth2Service.Parameters();
        parameters.setAuthorizationCode(oauth2Code);
        parameters.setRedirectUri(oauth2RedirectBackLink);
        parameters.setGrantType(grantType);
        return parameters;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aOauth2WithCodeParameters> {
        Xs2aOauth2WithCodeParameters map(Xs2aContext ctx);
    }
}
