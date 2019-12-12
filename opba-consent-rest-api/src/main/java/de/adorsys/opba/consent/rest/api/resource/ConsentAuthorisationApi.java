package de.adorsys.opba.consent.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.opba.consent.rest.api.domain.AuthorizeRequest;
import de.adorsys.opba.consent.rest.api.domain.AuthorizeResponse;
import de.adorsys.opba.consent.rest.api.domain.DenyRedirectRequest;
import de.adorsys.opba.consent.rest.api.domain.PsuAuthRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

/**
 * tags = "Authorization Entry Point", description = "Provides access to
 * ConsentAPI"
 *
 * @author fpo
 */
@SuppressWarnings("LineLength")
@Api(value = ConsentAuthorisationApi.PATH, tags = "Consent Authorization Api", description = "Entry point for processing a consent request redirected by the TppBankingApi over the FinTechApi to this ConsentAuthorisationApi.")
public interface ConsentAuthorisationApi {
  String COOKIE = "Cookie";
  String API_KEY = "apiKey";
  String PATH = "/consent";
  String CONSENT_SESSION_STATE = "consentSessionState";
  String CONSENT_SESSION_STATE_PATH = "/{" + CONSENT_SESSION_STATE + "}";

  @GetMapping(path = "/auth", params = { "redirectCode" })
  @ApiOperation(value = "Entry point for processing a consent request redirected by the TppBankingApi over the FinTechApi to this ConsentAuthorisationApi.", notes = "This is the <b>entry point</b> for processing a consent redirected by the TppBankingApi to this ConsentAuthorisationApi."
      + "<ul><b>TPP behavior prior to redirecting to the ConsentAuthorisationApi</b>"
      + "<li>The code is a one time string that contains information used to retrieve RedirectSession from the BankingProtocol in a back channel.</li>"
      + "<li>The code is short lived (like 10 seconds). This is, BankingProtocol does not need to hold the RedirectSession after expiration.</li>"
      + "<li>The code indexed record (RedirectSession) must also be deleted by the TPP on first retrieval by the ConsentAuthorisationApi.</li>"
      + "</ul>" + "<ul><b>Initial request processing ConsentAuthorisationApi</b>"
      + "<li>ConsentAuthorisationApi will use the code to retrieve the RedirectSession from the back channel BankingProtocol.</li>"
      + "<li>ConsentAuthorisationApi will generate an consentAuthState that is used to identify protect access to the ConsentSession.</li>"
      + "<li>All subsequent request to the ConsentAuthorisationApi must specify the consentAuthState.</li>" + "</ul>"
      + "<ul><b>Interacting with the PSU user agent</b>"
      + "<li>The consentAuthState is a transient reference of the ConsentSession. It is used to encrypt information stored in the corresponding ConsentAuthSessionCookie.</li>"
      + "<li>The retruned AuthorizeResponse object info is needed to display a qualified information page to the PSU prio to eventually redirecting the PSU to the target ASPSP.</li>"
      + "<li>The retruned AuthorizeResponse object allways carries the consentAuthState that is needed in any subsequent request to the ConsentAuthorisationApi. Therefore ConsentAuthorisationApi shall never"
      + " store the consentAuthState the ConsentAuthSessionCookie</li>"
      + "<li>The retruned AuthorizeResponse object is allways synchronized with the ConsentAuthSessionCookie set with the same HTTP response object.</li>"
      + "<li>The consentAuthState contained in the retruned AuthorizeResponse object is also used to decrypt information stored in the ConsentAuthSessionCookie set with the same HTTP response object.</li>"
      + "<li>Any session, account or payment information needed to manage the authorization process is stored in both AuthorizeResponse and encrypted in the ConsentAuthSessionCookie</li>"
      + "<li>The ConsentAuthSessionCookie is httpOnly</li>" + "</ul>" + "<ul><b>Redirecting PSU to the ASPSP</b>"
      + "<li>The retruned AuthorizeResponse object contains information needed to redirect the PSU to the target ASPSP.</li>"
      + "<li>The BackRedirectURL (OkUrl, NokUrl, etc... depending of ASPSP API) contains the consentAuthState.</li>"
      + "</ul>" + "<ul><b>Back-Redirecting PSU to the ConsentAuthorisationApi</b>"
      + "<li>The ASPSP BackRedirectURL used to redirect the PSU to the ASPSP contains the consentAuthState</li>"
      + "<li>The consentAuthState will the be used to retrieve the attached ConsentAuthSessionCookie containing further consent information.</li>"
      + "</ul>" + "<ul><b>Back-Redirecting PSU to the FinTechApi</b>"
      + "<li>Prior to redirecting the PSU back to the FinTechApi, consent information will in a be stored in a RedirectSession object by the BankingProtocol.</li>"
      + "<li>The one time resulting redirectCode will be used to redirect the PSU to the FinTechApi.</li>"
      + "<li>The RedirectSession will then retrieve the RedirectSession using the redirectCode and proceed forward with the authorization process.</li>"
      + "</ul>")
  ResponseEntity<AuthorizeResponse> authEntryPoint(@RequestParam(name = "redirectCode") String redirectCode);

  /**
   * Provides the ConsentAuthorisationApi with the opportunity to redirect the PSU
   * to the ASPSP.
   *
   * @param AuthorizeRequest         containing the consentSessionState
   * @param consentAuthSessionCookie The ConsentAuthSessionCookie
   * @return Void
   */
  @PostMapping(path = "/to/aspsp/grant")
  @ApiOperation(value = "Provides the ConsentAuthorisationApi with the opportunity to redirect the PSU to the ASPSP.", notes = "Provides the ConsentAuthorisationApi with the opportunity to redirect the PSU to the ASPSP."
      + "<ul><b>Request contains:</b>"
      + "<li>consentSessionState is retrieved from the AuthorizeResponse of the peceeding request.</li>"
      + "<li>ConsentAuthSessionCookie.</li>" + "</ul>" + "<ul><b>Returns:</b>" + "<li>302 Redirect</li>"
      + "<li>Redirect Location Header.</li>" + "<li>New ConsentAuthSessionCookie.</li>"
      + "</ul>", authorizations = @Authorization(value = API_KEY))
  ResponseEntity<Void> grantRedirect(@RequestHeader(name = COOKIE, required = false) String consentAuthSessionCookie,
      @RequestBody AuthorizeRequest request);

  /**
   * Provides the ConsentAuthorisationApi with the opportunity to close this
   * ConsentSession and redirect the PSU back to the TPP <b>OR</b> close the
   * application window.
   * <p>
   * In any case, the ConsentSession of the PSU will be terminated and cookies
   * will be deleted.
   *
   * @param DenyRedirectRequest      containing the exit page.
   * @param consentAuthSessionCookie The ConsentAuthSessionCookie
   * @return Void
   */
  @PostMapping(path = "/to/aspsp/deny")
  @ApiOperation(value = "Closes this session and redirects the PSU back to the FinTechApi or close the application window.", notes = "Closes this session and redirects the PSU back to the FinTechApi or close the application window. "
      + "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
      + "<ul><b>Request contains:</b>"
      + "<li>consentSessionState is retrieved from the AuthorizeResponse of the peceeding request.</li>"
      + "<li>ConsentAuthSessionCookie.</li>" + "</ul>" + "<ul><b>Returns:</b>" + "<li>302 Redirect</li>"
      + "<li>Redirect Location Header to tpp.</li>" + "<li>Null ConsentAuthSessionCookie (deletion).</li>"
      + "</ul>", authorizations = @Authorization(value = API_KEY))
  ResponseEntity<Void> denyRedirect(@RequestHeader(name = COOKIE, required = false) String consentAuthSessionCookie,
      @RequestBody DenyRedirectRequest request);

  /**
   * Redirecting back from ASPSP to ConsentAuthorisationApi after a successful
   * consent authorization.
   *
   * @param consentSessionState      The consent session state
   * @param consentAuthSessionCookie The consent cookie
   * @return Void
   */
  @GetMapping(path = "/from/aspsp" + CONSENT_SESSION_STATE_PATH + "/ok")
  @ApiOperation(value = "Redirecting back from ASPSP to ConsentAuthorisationApi after a successful consent authorization.", notes = "Redirecting back from ASPSP to ConsentAuthorisationApi after a successful consent authorization. "
      + "In any case, the consent session of the user will be closed and cookies will be deleted with the response to this request."
      + "<ul><b>Request contains:</b>"
      + "<li>consentSessionState included in the link sent to the ASPSP in the consent initiation.</li>"
      + "<li>Instructions on how to proceed with the next request is included in the ConsentAuthSessionCookie.</li>"
      + "</ul>" + "<ul><b>Returns:</b>" + "<li>302 Redirect</li>" + "<li>Redirect Location Header to FinTechApi.</li>"
      + "<li>Null ConsentAuthSessionCookie (deletion).</li>"
      + "</ul>", authorizations = @Authorization(value = API_KEY))
  ResponseEntity<AuthorizeResponse> fromAspspOk(@PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
      @RequestHeader(name = COOKIE, required = false) String consentAuthSessionCookie);

  /**
   * Redirecting back from ASPSP to TPP after a failed consent authorization.
   *
   * @param consentSessionState      The consent session state
   * @param consentAuthSessionCookie The consent cookie
   * @return Void
   */
  @GetMapping(path = "/from/aspsp" + CONSENT_SESSION_STATE_PATH + "/nok")
  @ApiOperation(value = "Redirecting back from ASPSP to TPP after a failed consent authorization.", notes = "Redirecting back from ASPSP to TPP after a failed consent authorization. "
      + "In any case, the session of the user will be closed and cookies will be deleted with the response to this request."
      + "<ul><b>Request contains:</b>"
      + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
      + "<li>Instructions on how to proceed with the next request is included in the ConsentAuthSessionCookie.</li>"
      + "</ul>" + "<ul><b>Returns:</b>" + "<li>302 Redirect</li>" + "<li>Redirect Location Header to FinTechApi.</li>"
      + "<li>Null ConsentAuthSessionCookie (deletion).</li>"
      + "</ul>", authorizations = @Authorization(value = API_KEY))
  ResponseEntity<AuthorizeResponse> fromAspspNok(@PathVariable(CONSENT_SESSION_STATE) String consentSessionState,
      @RequestHeader(name = COOKIE, required = false) String consentAuthSessionCookie);

  /**
   * Update consent session with PSU auth data whereby requesting remaining
   * challenges for the ongoing authorization process.
   *
   * @param consentSessionState      The consent session state
   * @param consentAuthSessionCookie The consent cookie
   * @param PsuAuthRequest           the PsuAuthRequest
   * @return AuthorizeResponse
   */
  @PostMapping(path = "/embedded/auth")
  @ApiOperation(value = "Generic challenge response end point for updating consent session with PSU authentication data while requesting remaining challenges for the ongoing authorization process.", notes = "Update consent session with PSU auth data whereby requesting remaining challenges for the ongoing authorization process."
      + "<ul><b>Request contains:</b>"
      + "<li>consentSessionState is retrieved from the response of the peceeding request.</li>"
      + "<li>The PsuAuthRequest constaining necessary consent info and auth data.</li>" + "</ul>"
      + "<ul><b>Returns:</b>" + "<li>The AuthorizeResponse constaining necessary consent info.</li>"
      + "</ul>", authorizations = @Authorization(value = API_KEY))
  ResponseEntity<AuthorizeResponse> embeddedAuth(
      @RequestHeader(name = COOKIE, required = false) String consentAuthSessionCookie,
      @RequestBody PsuAuthRequest request);
}
