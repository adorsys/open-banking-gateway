package de.adorsys.opba.protocol.services.psuconsentsession.redirect;


import de.adorsys.opba.protocol.services.psuconsentsession.PsuConsentSession;

public interface RedirectionService {
    RedirectionToConsentAuthApi redirectForAuthorisation(PsuConsentSession psuConsentSession);
}
