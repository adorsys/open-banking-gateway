package de.adorsys.opba.consent.embedded.rest.api.domain;

import java.util.List;

import de.adorsys.opba.consent.embedded.rest.api.domain.account.AccountDetailsTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.account.AisConsentTO;

public class ConsentAuthorizeResponse extends AuthorizeResponse  {
	private List<AccountDetailsTO> accounts;
	private AisConsentTO consent;
	private String authMessageTemplate;
	
	public ConsentAuthorizeResponse() {
	}
	
	public ConsentAuthorizeResponse(AisConsentTO consent) {
		super();
		this.consent = consent;
	}
	public String getAuthMessageTemplate() {
		return authMessageTemplate;
	}
	public void setAuthMessageTemplate(String authMessageTemplate) {
		this.authMessageTemplate = authMessageTemplate;
	}
	public AisConsentTO getConsent() {
		return consent;
	}

	public List<AccountDetailsTO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDetailsTO> accounts) {
		this.accounts = accounts;
	}

	public void setConsent(AisConsentTO consent) {
		this.consent = consent;
	}
	
}
