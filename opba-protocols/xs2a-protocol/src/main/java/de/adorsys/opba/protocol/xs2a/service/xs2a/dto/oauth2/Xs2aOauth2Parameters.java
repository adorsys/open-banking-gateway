package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.Oauth2Service;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class Xs2aOauth2Parameters {

    @NotBlank
    private String fromOauth2WithCode;

    @NotBlank
    private String state;

    // can be blank in pre-step (pre-Authentication), but either consentId/paymentId should be filled in integrated Oauth2
    private String consentId;

    // can be blank in pre-step (pre-Authentication), but either consentId/paymentId should be filled in integrated Oauth2
    private String paymentId;

    // TODO - MapStruct?
    public Oauth2Service.Parameters toParameters() {
        Oauth2Service.Parameters parameters = new Oauth2Service.Parameters();
        parameters.setRedirectUri(fromOauth2WithCode);
        parameters.setState(state);
        parameters.setConsentId(consentId);
        parameters.setPaymentId(paymentId);
        return parameters;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aOauth2Parameters> {
    }
}
