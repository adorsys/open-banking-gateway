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
     * Supported consent type (if any) by ASPSP.
     */
    List<SupportedConsentType> getSupportedConsentTypes();

    /**
     * ASPSP tries to use preferred SCA approach.
     */
    boolean isTryToUsePreferredApproach();

    /**
     * ASPSP supported Xs2a api version
     */

    String getSupportedXs2aApiVersion();

    /**
     * Current date will be added in the end of payment purpose if this field is true .
     */
    boolean isUniquePaymentPurpose();

    /**
     * Whether to try to skip call to ConsentAuthorization action (startAuthorization)
     */
    boolean isXs2aSkipConsentAuthorization();

    /**
     * Whether to start Consent Authorization with user Pin
     */
    boolean isXs2aStartConsentAuthorizationWithPin();

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
    String getBankName();

    /**
     * Bank external id (i.e. in system that is not OBG)
     */
    String getExternalId();

    /**
     * Bank external interfaces (i.e. in system that is not OBG)
     */
    String getExternalInterfaces();

    /**
     * Returns protocol configuration (3rd party) to be used by default.
     */
    String getProtocolConfiguration();

    /**
     * Expected result content type for this ASPSP.
     */
    ResultContentType getContentTypeTransactions();
}
