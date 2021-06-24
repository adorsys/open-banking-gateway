package de.adorsys.opba.protocol.api.common;

import java.util.List;

/**
 * View of current bank that is provided to protocol.
 */
public interface CurrentBankProfile {

    /**
     * Bank protocol ID.
     */
    Long getId();

    /**
     * ASPSP API endpoint URL for this protocol.
     */
    String getUrl();

    /**
     * The ID of ASPSP API adapter to use (if defined).
     */
    String getAdapterId();

    /**
     * Identity provider URL for ASPSP API.
     */
    String getIdpUrl();

    /**
     * Supported SCA authorization approaches by this ASPSP.
     */
    List<Approach> getScaApproaches();

    /**
     * Preferred SCA approach for this ASPSP.
     */
    Approach getPreferredApproach();

    /**
     * ASPSP tries to use preferred SCA approach.
     */
    boolean isTryToUsePreferredApproach();

    /**
     * Current date will be added in the end of payment purpose if this field is true .
     */
    boolean isUniquePaymentPurpose();

    /**
     * Whether to try to skip call to ConsentAuthorization action (startAuthorization)
     */
    boolean isXs2aSkipConsentAuthorization();

    /**
     * Bank identification code.
     */
    String getBic();

    /**
     * Bank code.
     */
    String getBankCode();

    /**
     * Bank name
     */
    String getName();

    /**
     * Bank external id (i.e. in system that is not OBG)
     */
    String getExternalId();

    /**
     * Bank external interfaces (i.e. in system that is not OBG)
     */
    String getExternalInterfaces();
}
