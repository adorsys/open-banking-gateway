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
    private String authorization;
    private String fintechUserID;
    private String fintechRedirectURLOK;
    private String fintechRedirectURLNOK;
    private UUID xRequestID;
    private String bankID;
    private String psuConsentSession;

    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
