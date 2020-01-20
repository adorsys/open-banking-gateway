package de.adorsys.opba.tppbanking.services.ais.account;

import de.adorsys.opba.tppbanking.services.psuconsentsession.PsuConsentSession;

import java.util.Optional;

public interface GetAccountsService {
    Optional<AccountsReport> getAccountsFor(PsuConsentSession psuConsentSession);
}
