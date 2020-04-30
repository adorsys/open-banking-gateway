package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import java.util.Map;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONSENT_ID;

/**
 * XS2A-adapter headers to be used after the consent was created.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aWithConsentIdHeaders extends Xs2aStandardHeaders {

    /**
     * Consent ID that is used for current operation.
     */
    @NotBlank // can't be provided manually
    private String consentId;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = super.toHeaders().toMap();

        allValues.put(CONSENT_ID, consentId);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aWithConsentIdHeaders> {
        Xs2aWithConsentIdHeaders map(Xs2aContext ctx);
    }
}
