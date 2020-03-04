package de.adorsys.opba.fintech.impl.service.mocks;

import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSession;
import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;

public class TppBankingApiTokenMock extends MockBaseClass {
    public PsuConsentSessionResponse getTransactionsResponse(String redirectCode) {
        PsuConsentSessionResponse resp = new PsuConsentSessionResponse();
        resp.psuConsentSession(new PsuConsentSession());

        return resp;
    }
}
