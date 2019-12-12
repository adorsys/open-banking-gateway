package de.adorsys.opba.consent.rest.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.adorsys.opba.consent.rest.api.domain.AuthorizeRequest;
import de.adorsys.opba.consent.rest.api.domain.AuthorizeResponse;
import de.adorsys.opba.consent.rest.api.domain.DenyRedirectRequest;
import de.adorsys.opba.consent.rest.api.domain.PsuAuthRequest;
import de.adorsys.opba.consent.rest.api.resource.ConsentAuthorisationApi;

@Controller
@RequestMapping(path=ConsentAuthorisationApi.PATH)
public class EntryPointImpl implements ConsentAuthorisationApi {

	@Override
	public ResponseEntity<AuthorizeResponse> authEntryPoint(String authCode) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> grantRedirect(String consentAuthSessionCookie, AuthorizeRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> denyRedirect(String consentAuthSessionCookie, DenyRedirectRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<AuthorizeResponse> fromAspspOk(String consentSessionState, String consentAuthSessionCookie) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<AuthorizeResponse> fromAspspNok(String consentSessionState, String consentAuthSessionCookie) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<AuthorizeResponse> embeddedAuth(String consentAuthSessionCookie, PsuAuthRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
