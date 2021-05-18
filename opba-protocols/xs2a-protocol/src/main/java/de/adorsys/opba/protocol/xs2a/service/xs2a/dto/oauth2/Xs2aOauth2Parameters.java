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
public class Xs2aOauth2Parameters {

    @NotBlank
    private String oauth2RedirectBackLink;

    @NotBlank
    private String state;

    private String scaOauthLink;

    // can be blank in pre-step (pre-Authentication), but either consentId/paymentId should be filled in integrated Oauth2
    private String consentId;

    // can be blank in pre-step (pre-Authentication), but either consentId/paymentId should be filled in integrated Oauth2
    private String paymentId;

    // Can be blank, only for ING
    private String scope;

    // TODO - MapStruct?
    public Oauth2Service.Parameters toParameters() {
        Oauth2Service.Parameters parameters = new Oauth2Service.Parameters();
        parameters.setRedirectUri(oauth2RedirectBackLink);
        parameters.setState(state);
        parameters.setConsentId(consentId);
        parameters.setPaymentId(paymentId);
        parameters.setScaOAuthLink(scaOauthLink);
        parameters.setScope(scope);
        return parameters;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aOauth2Parameters> {
    }
}
