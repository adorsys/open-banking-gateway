package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aAuthorizationHeaders extends Xs2aStandardHeaders {

    public static final Xs2aAuthorizationHeaders.FromCtx FROM_CTX = Mappers.getMapper(Xs2aAuthorizationHeaders.FromCtx.class);

    private String redirectUriOk;
    private String redirectUriNok;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = super.toHeaders().toMap();

        allValues.put(TPP_REDIRECT_URI, redirectUriOk);
        allValues.put(TPP_NOK_REDIRECT_URI, redirectUriNok);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper
    public interface FromCtx {
        Xs2aAuthorizationHeaders map(Xs2aContext ctx);
    }
}
