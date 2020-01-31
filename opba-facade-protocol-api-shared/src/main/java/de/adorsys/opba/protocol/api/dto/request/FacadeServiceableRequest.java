package de.adorsys.opba.protocol.api.dto.request;

import de.adorsys.opba.tppbankingapi.useragent.UserAgentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacadeServiceableRequest {

    private UserAgentContext uaContext;

    private UUID xRequestID;
    private String serviceSessionId;
    private String authSessionId;

    private String authorization;
    private String bankID;

    private String fintechUserID;
    private String fintechRedirectURLOK;
    private String fintechRedirectURLNOK;
}
