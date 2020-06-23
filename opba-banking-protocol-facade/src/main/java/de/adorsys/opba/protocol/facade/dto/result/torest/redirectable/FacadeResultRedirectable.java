package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Data
public abstract class FacadeResultRedirectable<T, C extends AuthStateBody> implements FacadeResult<T> {

    private String redirectCode;
    private UUID xRequestId;
    private String serviceSessionId;
    private String authorizationSessionId;
    private URI redirectionTo;
    private C cause;
    private String token;

    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }
}
