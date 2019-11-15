package de.adorsys.opba.consent.redirect.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import de.adorsys.opba.consent.common.rest.api.resource.AuthEntryPointApi;
import de.adorsys.opba.consent.embedded.rest.api.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Api(value = "/consent-redirect", tags = "PSU Consent Redirect API", description = "Provides access to redirect consent functionality")
public interface RedirectApi extends AuthEntryPointApi {
    public static final String API_KEY = "apiKey";
	public static final String COOKIE = "Cookie";
	public static final String CONSENT_SESSION_STATE = "consentSessionState";
	public static final String CORE_PATH = "/{consentSessionState}";

    /**
     * Redirecting back from ASPSP to TPP after a successful consent.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @return AuthorizeResponse
     */
    @GetMapping(path = CORE_PATH + "/aspspOK")
    @ApiOperation(value = "Redirecting back from ASPSP to TPP after a successful consent.",
        notes = "Redirecting back from ASPSP to TPP after a successful consent. "
    		+ "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "<li>Instructions on how to proceed with the next request is included in the consentCookieString.</li>"
            + "</ul>"
            + "<ul>Returned AuthorizeResponse contains:"
            + "<li>Redirection info.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<AuthorizeResponse> aspspOK(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString);

    /**
     * Redirecting back from ASPSP to TPP after a failed consent.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @return AuthorizeResponse
     */
    @GetMapping(path = CORE_PATH + "/aspspNOK")
    @ApiOperation(value = "Redirecting back from ASPSP to TPP after a failed consent.",
        notes = "Redirecting back from ASPSP to TPP after a failed consent. "
    		+ "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "<li>Instructions on how to proceed with the next request is included in the consentCookieString.</li>"
            + "</ul>"
            + "<ul>Returned AuthorizeResponse contains:"
            + "<li>Redirection info.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<AuthorizeResponse> aspspNOK(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString);
    
    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed and cookies will be deleted.
     *
     * @param consentSessionState The consent session state
     * @param consentCookieString The consent cookie
     * @return AuthorizeResponse
     */
    @GetMapping(path = CORE_PATH + "/scaDone")
    @ApiOperation(value = "Closes this session and redirects the PSU to the TPP or close the application window.",
        notes = "Closes this session and redirects the PSU to the TPP or close the application window. "
    		+ "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
            + "<ul>"
            + "<ul>Request contains:"
            + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
            + "</ul>"
            + "<ul>Returned AuthorizeResponse contains:"
            + "<li>Redirection info.</li>"
            + "</ul>"
            + "</ul>",
            authorizations = @Authorization(value = API_KEY))
    ResponseEntity<AuthorizeResponse> scaDone(
        @PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
        @RequestHeader(name = COOKIE, required = false) String consentCookieString);

}

