package de.adorsys.opba.protocol.services.ais.account;

import de.adorsys.opba.protocol.services.psuconsentsession.PsuConsentSession;

import java.util.Optional;

public interface GetAccountsService {
    Optional<AccountsReport> getAccountsFor(PsuConsentSession psuConsentSession);
}
