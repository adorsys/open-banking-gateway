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

    private final UUID requestId;
    private final UUID serviceSessionId;
    private final String authorizationSessionId;
    private final String redirectCode;

    private final String authorization;
    private final String bankId;

    private final String fintechUserId;
    private final String fintechRedirectUrlOk;
    private final String fintechRedirectUrlNok;

    private final String sessionPassword;
    private final String keyFromCookie;
}
