package de.adorsys.opba.protocol.api.dto.request.transactions;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.tppbankingapi.useragent.UserAgentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

// TODO Validation
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionsRequest {

    private UserAgentContext uaContext;
    private String serviceSessionId;
    private String validationSessionId;
    private String authSessionId;
    private UUID xRequestID;

    private String accountId;
    private String fintechUserID;
    private String authorization;
    private String fintechRedirectURLOK;
    private String fintechRedirectURLNOK;
    private String bankID;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String entryReferenceFrom;
    private String bookingStatus;
    private Boolean deltaList;

    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
