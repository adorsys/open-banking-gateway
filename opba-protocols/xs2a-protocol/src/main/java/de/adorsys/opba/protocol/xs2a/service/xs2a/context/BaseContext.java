package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
// FIXME Entire class must be protected https://github.com/adorsys/open-banking-gateway/issues/251
public class BaseContext {

    private ContextMode mode;
    // Application required
    private String aspspId;
    private ProtocolAction action;

    private Map<ProtocolAction, String> flowByAction = ImmutableMap.of(
            ProtocolAction.LIST_ACCOUNTS, "xs2a-list-accounts",
            ProtocolAction.LIST_TRANSACTIONS, "xs2a-list-transactions"
    );

    private String sagaId;
    // Used to find existing consent:
    private UUID serviceSessionId;

    /**
     * Read-only. This is for redirects from ASPSP, Facade provides this value.
     */
    private String authorizationSessionIdIfOpened;

    /**
     * Read-only. Facade provides this value.
     */
    private String redirectCodeIfAuthContinued;

    /**
     * Read-only. This is for redirects from ASPSP, Facade provides this value.
     */
    private String aspspRedirectCode;

    /**
     * GetAuth state variables
     */
    private Set<ValidationIssue> violations = new HashSet<>();
    private LastRedirectionTarget lastRedirection;
    private Boolean wrongAuthCredentials;

    /**
     * Other helpful functions
     */
    public String getRequestId() {
        return this.sagaId;
    }
}
