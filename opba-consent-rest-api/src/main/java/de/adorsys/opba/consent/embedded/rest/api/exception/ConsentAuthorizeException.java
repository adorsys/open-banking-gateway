package de.adorsys.opba.consent.embedded.rest.api.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.opba.consent.embedded.rest.api.domain.ConsentAuthorizeResponse;

public class ConsentAuthorizeException extends Exception {
	private static final long serialVersionUID = 7876974990567439886L;
	private final ResponseEntity<ConsentAuthorizeResponse> error;

	public ConsentAuthorizeException(ResponseEntity<ConsentAuthorizeResponse> error) {
		this.error = error;
	}

	public ResponseEntity<ConsentAuthorizeResponse> getError() {
		return error;
	}
}
