package de.adorsys.opba.protocol.api.dto.request.accounts;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.tppbankingapi.useragent.UserAgentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

// TODO Validation
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountsRequest {

    private UserAgentContext uaContext;
    private String serviceSessionId;
    private String validationSessionId;
    private String authSessionId;
    private UUID xRequestID;

    private String authorization;
    private String fintechUserID;
    private String fintechRedirectURLOK;
    private String fintechRedirectURLNOK;
    private String bankID;

    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
