package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.RequestParams;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Parameters (path) that are used after consent authorization was started, that are necessary to call XS2A-adapter.
 */
@Data
public class Xs2aAuthorizedConsentParameters {

    /**
     * Consent ID that is used for current operation. (Authorized for)
     */
    @NotBlank // can't be provided manually
    private String consentId;

    /**
     * Authorization ID - ASPSP authorization session ID.
     */
    @NotBlank // can't be provided manually
    private String authorizationId;

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aAuthorizedConsentParameters> {
        Xs2aAuthorizedConsentParameters map(Xs2aContext ctx);
    }

    public RequestParams toParameters() {
        return RequestParams.builder()
                .build();
    }
}
