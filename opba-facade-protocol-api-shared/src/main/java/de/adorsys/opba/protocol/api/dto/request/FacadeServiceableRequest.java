package de.adorsys.opba.protocol.api.dto.request;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class FacadeServiceableRequest {

    private final UserAgentContext uaContext;

    private final UUID xRequestID;
    private final UUID serviceSessionId;
    private final String authorizationSessionId;
    private final String redirectCode;

    private final String authorization;
    private final String bankID;

    private final String fintechUserID;
    private final String fintechRedirectURLOK;
    private final String fintechRedirectURLNOK;
}
