package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONSENT_ID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aWithConsentIdHeaders extends Xs2aStandardHeaders {

    public static final FromCtx FROM_CTX = Mappers.getMapper(FromCtx.class);

    private String consentId;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = super.toHeaders().toMap();

        allValues.put(CONSENT_ID, consentId);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper
    public interface FromCtx {
        Xs2aWithConsentIdHeaders map(Xs2aContext ctx);
    }
}
