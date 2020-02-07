package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;

import java.net.URI;
import java.util.UUID;

@Data
public abstract class FacadeResultRedirectable<T> implements FacadeResult<T> {

    private String redirectCode;
    private UUID xRequestId;
    private String serviceSessionId;
    private String authorizationSessionId;
    private URI redirectionTo;
}
