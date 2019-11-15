package de.adorsys.opba.consent.embedded.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.opba.consent.common.rest.api.resource.AuthEntryPointApi;
import de.adorsys.opba.consent.embedded.rest.api.domain.AuthorizeResponse;
import de.adorsys.opba.consent.embedded.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.opba.consent.embedded.rest.api.domain.PsuAuthDataTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.PsuScaAuthDataTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.PsuScaDoneDataTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.SelectedPsuAuthMethodTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.account.AisConsentTO;
import de.adorsys.opba.consent.embedded.rest.api.exception.ConsentAuthorizeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Api(value = "/consent-embedded-ais", tags = "PSU AIS Consent", description = "Provides access to embedded (tpp driven) AIS consent functionality")
public interface AISEmbeddedApi extends AuthEntryPointApi {
    public static final String API_KEY = "apiKey";
	public static final String COOKIE = "Cookie";
	public static final String CONSENT_SESSION_STATE = "consentSessionState";
	public static final String CORE_PATH = "/{consentSessionState}";

    /**
     * Identifies the user by login an pin.
     * <p>
     * The returned object contains:
     * <ul>
     * <li>A list of accounts
     * <p>This is supposed to be used to display the list of accounts to the psu</p>
     * </li>
     * <li>An AisConsent object
     * <p>This consent is initialized, but might not contain any more information</p>
     * </li>
     * </ul>
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param PsuAuthDataTO  		auth data
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = CORE_PATH + "/psuAuth")
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information",
        notes = "Identifies the user by login an pin. Return sca methods information."
                + "<ul>"
                + "<ul>Request contains:"
                + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
                + "<li>PsuAuthDataTO contain PSU authentication data. Like bank user name, bainking pin, corporate id.</li>"
                + "<li>The sent consentCookieString contains consent information and and the match of the consentSessionState</li>"
                + "</ul>"
                + "<ul>Returned ConsentAuthorizeResponse contains:"
                + "<li>A list of accounts. This is supposed to be used to display the list of accounts to the PSU for eventual selection.</li>"
                + "<li>An AisConsent object. This consent is initialized, but might not contain any more information.</li>"
                + "</ul>"
                + "</ul>")
    ResponseEntity<ConsentAuthorizeResponse> psuAuthentication(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
        @RequestBody PsuAuthDataTO authData);

    /**
     * Starts the consent authorization process after PSU selects which account to grant access to.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param AisConsentTO   the consent request object
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(CORE_PATH + "/startAuth")
    @ApiOperation(value = "Starts the consent authorization process after PSU selects which account to grant access to.",
        notes = "Starts the consent authorization process after PSU selects which account to grant access to."
                + "<ul>"
                + "<ul>Request contains:"
                + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
                + "<li>The given AisConsent object contains the user selection of account subject of PSU consent.</li>"
                + "<li>The sent consentCookieString contains consent information and and the match of the consentSessionState</li>"
                + "</ul>"
                + "<ul>Returned ConsentAuthorizeResponse contains:"
                + "<li>A list of accounts. This is supposed to be used to display the list of accounts to the PSU for eventual selection.</li>"
                + "<li>The AisConsent object containing the selection of the user.</li>"
                + "</ul>"
                + "</ul>",
                authorizations = @Authorization(value = API_KEY))
    ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
        @RequestBody AisConsentTO aisConsent);


    /**
     * Selects the SCA Method for use.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param SelectedPsuAuthMethodTO  Holds the selected SCA method
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(CORE_PATH + "/scaAuthMethod")
    @ApiOperation(value = "Selects the SCA Method for use.", 
        notes = "Selects the SCA Method for use."
                + "<ul>"
                + "<ul>Request contains:"
                + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
                + "<li>SelectedPsuAuthMethodTO containing the selected PSU authentication method.</li>"
                + "<li>The sent consentCookieString contains consent information and and the match of the consentSessionState</li>"
                + "</ul>"
                + "<ul>Returned ConsentAuthorizeResponse contains:"
                + "<li>The AisConsent object containing the state of account access permissions being subject of the consent.</li>"
                + "</ul>"
                + "</ul>",
                authorizations = @Authorization(value = API_KEY))
    ResponseEntity<ConsentAuthorizeResponse> scaAuthMethod(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
        @RequestBody SelectedPsuAuthMethodTO scaAuthMethod);

    /**
     * Provides sca data for the validation of an authorization
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param PsuScaAuthDataTO The authentication code
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = CORE_PATH + "/scaAuthData")
    @ApiOperation(value = "Provides sca data for the legitimation  of a consent.", 
        notes = "Provides sca data for the legitimation  of a consent."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "<li>PsuScaAuthDataTO containing the provided PSU authentication data.</li>"
            + "</ul>"
            + "<ul>Returned ConsentAuthorizeResponse contains:"
            + "<li>The AisConsent object containing the state of account access permissions being subject of the consent.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
        @RequestBody PsuScaAuthDataTO scaAuthData);


    /**
     * Revokes the consent and closes the session. Eventually redirect PSU back to the TPP.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param PsuScaDoneDataTO Redirection preferences of the user agent.
     * @return ConsentAuthorizeResponse
     */
    @DeleteMapping(path = CORE_PATH)
    @ApiOperation(value = "Revokes the consent and close the session. Eventually redirect PSU back to the TPP.",
        notes = "Revokes the consent and close the session. Eventually redirect PSU back to the TPP."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "<li>PsuScaDoneDataTO containing instructions on how to proceed with the next request.</li>"
            + "<ul>Client can opt to"
            + "<li>Be automatically redirected back to the TPP.</li>"
            + "<li>Be given tpp redirection informations.</li>"
            + "<li>Not to be redirected or provided redirection info at all.</li>"
            + "</ul>"
            + "</ul>"
            + "<ul>Returned ConsentAuthorizeResponse contains:"
            + "<li>A ConsentAuthorizeResponse eventually containing redirection info.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<ConsentAuthorizeResponse> revokeConsent(
    	@PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
    	@RequestBody PsuScaDoneDataTO scaDoneData) throws ConsentAuthorizeException;
    
    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed and cookies will be deleted.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @param PsuScaDoneDataTO  Redirection preferences of the user agent.
     * @return ConsentAuthorizeResponse
     */
    @GetMapping(path = CORE_PATH + "/scaDone")
    @ApiOperation(value = "Closes this session and redirects the PSU to the TPP or close the application window.",
        notes = "Closes this session and redirects the PSU to the TPP or close the application window. "
    		+ "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "<li>PsuScaDoneDataTO containing instructions on how to proceed with the next request.</li>"
            + "<ul>Client can opt to"
            + "<li>Be automatically redirected back to the TPP.</li>"
            + "<li>Be given tpp redirection informations.</li>"
            + "<li>Not to be redirected or provided redirection info at all.</li>"
            + "</ul>"
            + "</ul>"
            + "<ul>Returned ConsentAuthorizeResponse contains:"
            + "<li>Redirection info.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<ConsentAuthorizeResponse> scaDone(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString,
        @RequestBody PsuScaDoneDataTO scaDoneData) throws ConsentAuthorizeException;

}

