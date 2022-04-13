package de.adorsys.opba.protocol.api.dto.request;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * The request specification that is serviced by Facade.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class FacadeServiceableRequest {

    /**
     * User agent context that describes users' interaction medium.
     */
    private final UserAgentContext uaContext;

    /**
     * Request identifier for tracing. Is a {@code de.adorsys.opba.restapi.shared.Headers.X_REQUEST_ID} header
     */
    private final UUID requestId;

    /**
     * Identifier of current service session that is responsible for the request processing.
     * Is {@code de.adorsys.opba.db.domain.entity.sessions.ServiceSession} entity ID.
     */
    private final UUID serviceSessionId;

    /**
     * Identifier of current authorization session that is responsible for the request processing.
     * Is {@code de.adorsys.opba.db.domain.entity.sessions.AuthSession} entity ID.
     */
    private final String authorizationSessionId;

    /**
     * Redirect code that is used as session stage identifier to ensure forward-only advance of the process.
     */
    private final String redirectCode;

    /**
     * Currently, is the Fintech ID.
     */
    private final String authorization;

    /**
     * ASPSP profile ID for this request that clearly specifies which protocol is used.
     * Is {@code de.adorsys.opba.db.domain.entity.BankProfile} entity uuid.
     */
    private final UUID bankProfileId;

    /**
     * User login within Fintech.
     */
    private final String fintechUserId;

    /**
     * Fintech PSU back redirect URL when consent was acquired.
     */
    private final String fintechRedirectUrlOk;

    /**
     * Fintech PSU back redirect URL when consent was declined or its acquisition aborted.
     */
    private final String fintechRedirectUrlNok;

    /**
     * FinTech password to protect the session.
     */
    private final String sessionPassword;

    /**
     * Consent session password. Is provided by a cookie (typically)
     */
    private final String authorizationKey;

    /**
     * Users' consent id encryption key to protect from unauthorized access.
     */
    private final String psuAspspKeyId;

    /**
     * Allows to skip user login form to OpenBanking to perform payment.
     */
    private final boolean anonymousPsu;

    /**
     * When false then account or transaction list will be loaded from cache. Otherwise cache will be updated with new data.
     */
    private final boolean online;

    /**
     * For transaction listing requests triggers transaction analyzers to enrich online result.
     */
    private final Analytics withAnalytics;


    /**
     * This header might be used by Fintechs to inform the ASPSP about the brand used by the Fintech towards the PSU.
     */
    private final String fintechBrandLoggingInformation;

    /**
     *  URI for the Endpoint of the Fintech-API to which the status of the payment initiation should be sent.
     */
    private final String fintechNotificationURI;

    /**
     * The string has the formstatus=X1, ..., Xn where Xi is one of the constants SCA, PROCESS, LAST and where constants are not repeated.
     */
    private final String fintechNotificationContentPreferred;

    /**
     * If it equals "true", the Fintech prefers a decoupled SCA approach
     */
    private final boolean fintechDecoupledPreferred;
}
