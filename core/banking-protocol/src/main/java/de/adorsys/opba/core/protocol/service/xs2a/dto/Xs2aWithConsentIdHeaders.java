package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import java.util.Map;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONSENT_ID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aWithConsentIdHeaders extends Xs2aStandardHeaders {

    @NotBlank // can't be provided manually
    private String consentId;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = super.toHeaders().toMap();

        allValues.put(CONSENT_ID, consentId);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx {
        Xs2aWithConsentIdHeaders map(Xs2aContext ctx);
    }
}
