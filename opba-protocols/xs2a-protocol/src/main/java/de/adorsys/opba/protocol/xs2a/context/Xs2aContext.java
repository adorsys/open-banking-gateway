package de.adorsys.opba.protocol.xs2a.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataEntry;
import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.api.model.ChallengeData;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * Generic XS2A context
 */
// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aContext extends BaseContext {

    ///////////////////////////////////////// Mandatory static
    /**
     * PSU user ID (login) in ASPSP API
     */
    private String psuId;

    /**
     * Requested content type.
     */
    private String contentType = "application/json";

    /**
     * Hardcoded process names based on the action name.
     */
    private Map<ProtocolAction, String> flowByAction = ImmutableMap.of(
            ProtocolAction.LIST_ACCOUNTS, "xs2a-list-accounts",
            ProtocolAction.LIST_TRANSACTIONS, "xs2a-list-transactions",
            ProtocolAction.SINGLE_PAYMENT, "xs2a-single-payments"
    );

    ///////////////////////////////////////// Mandatory dynamic
    /**
     * PSU IP address - IP address of PSU device/browser that is used for consent authorization.
     */
    private String psuIpAddress; // FIXME https://github.com/adorsys/open-banking-gateway/issues/251

    ///////////////////////////////////////// In-process
    /**
     * Selected consent authorization approach (i.e. EMBEDDED).
     */
    private String aspspScaApproach;

    /**
     * Consent create response from ASPSP.
     */
    private ConsentsResponse201 consentCreateResponse;

    /**
     * ASPSP response after consent authorization was initiated. Used to retrieve ASPSP redirection link for
     * consent authorization for REDIRECT consent authorization.
     */
    private StartScaprocessResponse startScaProcessResponse;

    /**
     * Consent ID that uniquely identifies the consent within ASPSP. Highly sensitive field.
     */
    private String consentId;

    /**
     * Authorization ID (ASPSP facing) to use for current authorization session.
     */
    private String authorizationId;

    /**
     * Current status of consent authorization (consent authorization stage)
     */
    private String scaStatus;

    /**
     * Available SCA methods (i.e. SMS,email) for consent SCA challenge (2FA/multifactor authorization - 2nd factor)
     */
    private List<ScaMethod> availableSca;

    /**
     * The ID of SCA method that was selected by the user.
     */
    private String userSelectScaId;

    /**
     * SCA method that was selected if only one SCA method is available (automatically by ASPSP)
     */
    private AuthenticationObject scaSelected;

    /**
     * WebHook that will be called by ASPSP (and user will be redirected to) if the consent was <b>granted</b> during REDIRECT
     * authorization.
     */
    // TODO: protect from overriding using reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriOk;

    /**
     * WebHook that will be called by ASPSP (and user will be redirected to) if the consent was <b>declined</b> during REDIRECT
     * authorization.
     */
    // TODO: protect from overriding reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriNok;

    /**
     * FinTech WebHook that will be called by OpenBanking (and user will be redirected to) if the consent
     * was <b>granted</b> during authorization.
     */
    private String fintechRedirectUriOk;

    /**
     * FinTech WebHook that will be called by OpenBanking (and user will be redirected to) if the consent
     * was <b>declined</b> during authorization.
     */
    private String fintechRedirectUriNok;

    /**
     * Indicates was the consent <b>granted</b> or <b>declined</b> during REDIRECT authorization, after
     * ASPSP has called one of these webhooks:
     * <ul>
     *     <li>{@link Xs2aContext#redirectUriOk}</li>
     *     <li>{@link Xs2aContext#redirectUriNok}</li>
     * </ul>
     */
    private boolean redirectConsentOk;

    /**
     * IP port of IP address between PSU and TPP.
     */
    private String psuIpPort;

    /**
     * Is used for embedded SCA with some data to send back to PSU (for example in case of photo tan)
     */
    private ChallengeData challengeData;

    /**
     * Is used to store Oauth2 token in case of Oauth2 approaches.
     */
    private String oauth2Code;

    /**
     * Is used to store Oauth2 token in case of Oauth2 approaches.
     */
    private TokenResponse oauth2Token;

    /**
     * Indicates that ASPSP requires Oauth2-pre-step for consent authorization.
     */
    private boolean oauth2PreStepNeeded;

    /**
     * Indicates that ASPSP requires Oauth2-integrated step for consent authorization.
     */
    private boolean oauth2IntegratedNeeded;

    /**
     * SCA Oauth2 link to follow.
     */
    private String scaOauth2Link;

    /**
     * OAuth2 redirect back link that is used for this session.
     */
    private String oauth2RedirectBackLink;

    @JsonIgnore
    public String getPsuPassword() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getPsuPassword() : null;
    }

    @JsonIgnore
    public String getLastScaChallenge() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getScaChallengeResult() : null;
    }

    @JsonIgnore
    public void setPsuPassword(String psuPassword) {
        this.transientStorage().set(new TransientDataEntry(psuPassword, null));
    }

    @JsonIgnore
    public void setLastScaChallenge(String scaChallengeResult) {
        this.transientStorage().set(new TransientDataEntry(null, scaChallengeResult));
    }

    @JsonIgnore
    public Approach getActiveScaApproach() {
        return this.getAspspScaApproach() == null
                ?  this.getRequestScoped().aspspProfile().getPreferredApproach()
                : Approach.valueOf(this.getAspspScaApproach());
    }
}
