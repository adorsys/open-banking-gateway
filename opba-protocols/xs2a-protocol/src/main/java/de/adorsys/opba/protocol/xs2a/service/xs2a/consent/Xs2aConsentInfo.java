package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.api.common.Approach.DECOUPLED;
import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.consent.ConsentConst.CONSENT_FINALIZED;

/**
 * Generic information service about the consent based on current context.
 */
@Service("xs2aConsentInfo")
public class Xs2aConsentInfo {

    /**
     * Is the current consent authorization in EMBEDDED mode.
     */
    public boolean isEmbedded(Xs2aContext ctx) {
        return EMBEDDED.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }


    /**
     * Is the authorization already started.
     */
    public boolean isAuthorisationStarted(Xs2aContext ctx) {
        return !Strings.isNullOrEmpty(ctx.getAuthorizationId());
    }

    /**
     * Is the current consent authorization in REDIRECT mode.
     */
    public boolean isRedirect(Xs2aContext ctx) {
        return REDIRECT.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    /**
     * If ASPSP needs startConsentAuthorization to be skipped.
     */
    public boolean isTrySkipStartConsentAuthorization(Xs2aContext ctx) {
        return ctx.aspspProfile().isXs2aSkipConsentAuthorization();
    }

    /**
     * If ASPSP needs startConsentAuthorization with User Password.
     */
    public boolean isStartConsentAuthorizationWithPin(Xs2aContext ctx) {
        return ctx.aspspProfile().isXs2aStartConsentAuthorizationWithPin();
    }

    /**
     * Is the current consent authorization in OAUTH (not OAUTH pre-step) mode.
     */
    public boolean isOauth2Authorization(Xs2aContext ctx) {
        return ctx.isOauth2IntegratedNeeded();
    }

    /**
     * Is the current consent in OAUTH-Pre-step (authentication) mode.
     */
    public boolean isOauth2AuthenticationPreStep(Xs2aContext ctx) {
        return ctx.isOauth2PreStepNeeded() || ctx.isEmbeddedPreAuthNeeded();
    }

    /**
     * Is the Oauth2 pre-step or authorization required
     */
    public boolean isOauth2Required(Xs2aContext ctx) {
        return isOauth2Authorization(ctx) || isOauth2AuthenticationPreStep(ctx);
    }

    /**
     * Is the Oauth2 pre-step or authorization required
     */
    public boolean isEmbeddedPreAuthNeeded(Xs2aContext ctx) {
        return ctx.isEmbeddedPreAuthNeeded();
    }

    /**
     * Is the Oauth2 token available and ready to use (not expired)
     */
    public boolean isOauth2TokenAvailableAndReadyToUse(Xs2aContext ctx) {
        // FIXME - Token validity check
        return null != ctx.getOauth2Token();
    }

    public boolean isTrySkipStartPsuAuthentication(Xs2aContext ctx) {
        return isOauth2TokenAvailableAndReadyToUse(ctx) || ctx.aspspProfile().isXs2aSkipConsentPsuAuthentication();
    }

    /**
     * Is the current consent authorization in DECOUPLED mode.
     */
    public boolean isDecoupled(Xs2aContext ctx) {
        return DECOUPLED.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    public boolean isDecoupledWithZeroSca(Xs2aContext ctx) {
        return isDecoupled(ctx) && isZeroScaAvailable(ctx);
    }

    /**
     * Is selected SCA method decoupled
     */
    public boolean isDecoupledScaSelected(Xs2aContext ctx) {
        return ctx.isSelectedScaDecoupled();
    }

    /**
     * Is the current consent authorization using multiple SCA methods (SMS,email,etc.)
     */
    public boolean isMultipleScaAvailable(Xs2aContext ctx) {
        return null != ctx.getAvailableSca() && !ctx.getAvailableSca().isEmpty();
    }

    /**
     * Is decoupled SCA was finalised by PSU with mobile or other type of device
     */
    public boolean isDecoupledScaFinalizedByPSU(Xs2aContext ctx) {
        return ctx.isDecoupledScaFinished();
    }

    /**
     * Is decoupled SCA was failed (i.e. took too long)
     */
    public boolean isDecoupledScaFailed(Xs2aContext ctx) {
        return false; // FIXME - check if authorization is taking too much time
    }

    /**
     * Is the current consent authorization using zero SCA flow
     */
    public boolean isZeroScaAvailable(Xs2aContext ctx) {
        return (null == ctx.getAvailableSca()
                || null != ctx.getAvailableSca() && ctx.getAvailableSca().isEmpty())
                && null == ctx.getScaSelected();
    }

    /**
     * Is the PSU password present in the context.
     */
    public boolean isPasswordPresent(Xs2aContext ctx) {
        return null != ctx.getPsuPassword() && !isOauthEmbeddedPreStepDone(ctx);
    }

    /**
     * Is Oauth Embedded pre step already Done.
     */
    public boolean isOauthEmbeddedPreStepDone(Xs2aContext ctx) {
        return ctx.isEmbeddedPreAuthDone();
    }

    /**
     * Is the consent authorized and approved.
     */
    public boolean isConsentFinalized(Xs2aContext ctx) {
        return CONSENT_FINALIZED.equalsIgnoreCase(ctx.getScaStatus());
    }

    /**
     * Was the PSU password that was sent to ASPSP wrong.
     */
    public boolean isWrongPassword(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    /**
     * Was the SCA challenge result that was sent to ASPSP wrong.
     */
    public boolean isWrongScaChallenge(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    /**
     * Was the redirection from ASPSP in REDIRECT mode using OK (consent granted) or NOK url (consent denied).
     */
    public boolean isOkRedirectConsent(Xs2aContext ctx) {
        return ctx.isRedirectConsentOk();
    }

    /**
     * Generic wrong credentials indicator.
     */
    public boolean hasWrongCredentials(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }
}
