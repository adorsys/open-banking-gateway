package de.adorsys.opba.consent.common.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.opba.consent.embedded.rest.api.domain.AuthorizeResponse;
import io.swagger.annotations.ApiOperation;

/**
 * tags = "Authorization Entry Point", description = "Provides access to ConsentAPI"
 * 
 * @author fpo
 *
 */
public interface AuthEntryPointApi {

	@GetMapping(path="/auth", params= {"code"})
    @ApiOperation(value = "Entry point for processing a consent request redirected by the TPP Banking API over the FinTechAPI to the ConsentAPI.",
            notes = "This is the <b>entry point</b> for authenticating a consent redirected by the TPP server to the ConsentAPI."
                    + "<ul>"
                    + "<ul> TPP behavior prior to redirecting to the ConsentAPI"
                    + "<li>The code is a one time string that contains information used to retrieve redirectInfo from the TPP Server in a back channel.</li>"
                    + "<li>The code is short lived (like 10 seconds). This is, tPP server does not need to hold the consent session after expiration.</li>"
                    + "<li>The code indexed record must also be deleted by the TPP on first retrieval by the ConsentAPI.</li>"
                    + "</ul>"
                    + "<ul> Initial request processing ConsentAPI"
                    + "<li>ConsentAPI will use the code to retrieve the ConsentRecord from the back channel TPP endpoint.</li>"
                    + "<li>ConsentAPI will generate an consent-session-state that is used to identify this consent session</li>"
                    + "<li>All subsequent request to the ConsentAPi must specify the consent session.</li>"
                    + "</ul>"
                    + "<ul> Interacting with the PSU user agent"
                    + "<li>The consent-session-state is a transient reference of the consent request. It is used to encrypt information stored in the corresponding consentCookieString.</li>"
                    + "<li>The retruned AuthorizeResponse object info is needed to display a qualified information page to the PSU prio to redirecting the PSU to the target ASPSP.</li>"
                    + "<li>The retruned AuthorizeResponse object allways carries the consent-session-state that is needed in any subsequent request to the ConsentAPI. Therefore ConsentAPI shall never store the consent-session-state the consentCookieString</li>"
                    + "<li>The retruned AuthorizeResponse object is allways synchronized with the consentCookieString set with the same HTTP response object.</li>"
                    + "<li>The retruned AuthorizeResponse object is also used to decrypt information stored in the consentCookieString set with the same HTTP response object.</li>"
                    + "<li>Any session, account or payment information needed to manage the authorization process is stored in both AuthorizeResponse and encrypted in the consentCookieString</li>"
                    + "<li>The consentCookieString is httpOnly</li>"
                    + "</ul>"
                    + "<ul> Redirecting PSU to the ASPSP"
                    + "<li>The retruned AuthorizeResponse object info information needed to redirect the PSU to the target ASPSP.</li>"
                    + "<li>BackRedirectURL (OKUrl, NOKURL, etc... dependent of ASPSP API) contains the consent-session-state</li>"
                    + "</ul>"
                    + "<ul> Back-Redirecting PSU to the ConsentAPI"
                    + "<li>The ASPSP url used to redirect the PSU to the ASPSP contains the consent-session-state</li>"
                    + "<li>The consent-session-state will the be used to retrieve the attached consentCookieString and retriev information needed to redirect the PSU to the TPP</li>"
                    + "</ul>"
                    + "<ul> Back-Redirecting PSU to the TPP"
                    + "<li>Prior to redirecting the PSU to the TPP, consent information will be stored through the backchane,l of the TPP</li>"
                    + "<li>The one time resulting authCode qill be used to redirect the consent session to the TPP.</li>"
                    + "<li>The TPP will then retrieve the consentSession using the authCode and proceed forward with the authorization process.</li>"
                    + "</ul>"
                    + "</ul>"
    )
    ResponseEntity<AuthorizeResponse> aisAuth(@RequestParam(name = "code") String authCode);
}