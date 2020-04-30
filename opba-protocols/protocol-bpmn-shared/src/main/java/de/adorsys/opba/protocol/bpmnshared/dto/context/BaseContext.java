package de.adorsys.opba.protocol.bpmnshared.dto.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
import lombok.Data;
import lombok.experimental.Delegate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * General context. Represents general knowledge about currently executed request,
 * for example, contains outcome results from previous requests as well as the user input.
 */
@Data
// FIXME Entire class must be protected https://github.com/adorsys/open-banking-gateway/issues/251
public class BaseContext implements RequestScoped, UsesRequestScoped {

    /**
     * Is this context used for:
     * <ul>
     *     <li>process validation (checks all data necessary to do API call present? - {@link ContextMode#MOCK_REAL_CALLS})</li>
     *     <li>real ASPSP API call sequence {@link ContextMode#REAL_CALLS}</li>
     * </ul>
     */
    private ContextMode mode;

    /**
     * ASPSP ID (same as XS2A-adapter uses) that define the called bank in xs2a-adapter world.
     */
    private String aspspId;

    /**
     * Currently executed action.
     */
    private ProtocolAction action;

    /**
     * Process identifier - used to uniquely identify current process execution.
     */
    private String sagaId;

    /**
     * Current request ID (X-Request-ID).
     */
    private String requestId;

    ///////////////////////////////////////// Used to find existing consent:
    /**
     * Session that is associated with current consent.
     */
    private UUID serviceSessionId;

    /**
     * Read-only. Authorization session ID that will be used by facade if authorization will be started. Is needed
     * as ID (for OK/NOK url in ASPSP API call and to redirect user to consent authorization page)
     * is required before the session is created.
     */
    private String authorizationSessionIdIfOpened;

    /**
     * Read-only. Authorization session redirect code that will be used by facade if authorization will be started. Is needed
     * as code (to redirect user to consent authorization page) is required before the session is created. Acts as session step
     * identifier.
     */
    private String redirectCodeIfAuthContinued;

    /**
     * Read-only. Authorization session ID that will be used by facade if authorization will be started. Is needed
     * as code (for OK/NOK url in ASPSP API call) is required before the session is created. Protects session state
     * when returning from ASPSP.
     */
    private String aspspRedirectCode;

    ///////////////////////////////////////// GetAuth state variables
    /**
     * Latest validation issues of this context - parameters that are required to be supplied by suer.
     */
    private Set<ValidationIssue> violations = new HashSet<>();
    /**
     * Where last redirection is pointing to. Used to finish authorization when process has ended.
     */
    private LastRedirectionTarget lastRedirection;
    /**
     * Flag to indicate that last provided credentials (SCA challenge/TAN, PIN/password) were wrong
     */
    private Boolean wrongAuthCredentials;

    /**
     * Request-scoped services and data.
     */
    @Delegate
    @JsonIgnore
    private RequestScoped requestScoped;
}
