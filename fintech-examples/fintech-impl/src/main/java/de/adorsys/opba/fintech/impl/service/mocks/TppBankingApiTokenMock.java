package de.adorsys.opba.fintech.impl.service.mocks;

import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSession;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;

public class TppBankingApiTokenMock extends MockBaseClass {
    public PsuConsentSessionResponse getTransactionsResponse(String redirectCode) {
        PsuConsentSessionResponse resp = new PsuConsentSessionResponse();
        resp.psuConsentSession(new PsuConsentSession());

        return resp;
    }
}
