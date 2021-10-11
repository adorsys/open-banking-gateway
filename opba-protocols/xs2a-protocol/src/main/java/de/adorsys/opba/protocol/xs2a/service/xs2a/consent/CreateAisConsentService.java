package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.quirks.QuirkUtil;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import org.springframework.stereotype.Service;

/**
 * Calls Xs2a API to initiate AIS consent.
 */
@Service
public class CreateAisConsentService {

    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    Response<ConsentsResponse201> createConsent(
            AccountInformationService ais,
            Xs2aAisContext context,
            ValidatedPathHeadersBody<ConsentInitiateParameters, ConsentInitiateHeaders, Consents> params) {
        logResolver.log("createConsent with parameters: {}", params.getPath(), params.getHeaders(), params.getBody());
        Response<ConsentsResponse201> consentInit = ais.createConsent(
                QuirkUtil.pushBicToXs2aAdapterHeaders(context, params.getHeaders().toHeaders()),
                params.getPath().toParameters(),
                params.getBody()
        );
        logResolver.log("createConsent response: {}", consentInit);
        return consentInit;
    }
}
