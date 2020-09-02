package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.adapter.link.bg.template.LinksTemplate.SCA_OAUTH;

/**
 * Calls Xs2a API to initiate AIS consent.
 */
@Service
public class CreateAisConsentService {
    void createConsent(
            AccountInformationService ais,
            DelegateExecution execution,
            Xs2aAisContext context,
            ValidatedHeadersBody<ConsentInitiateHeaders, Consents> params) {

        Response<ConsentCreationResponse> consentInit = ais.createConsent(
                params.getHeaders().toHeaders(),
                params.getBody()
        );

        context.setWrongAuthCredentials(false);
        context.setConsentId(consentInit.getBody().getConsentId());
        if (null != consentInit.getBody().getLinks() && consentInit.getBody().getLinks().containsKey(SCA_OAUTH)) {
            context.setOauth2IntegratedNeeded(true);
            context.setScaOauth2Link(consentInit.getBody().getLinks().get(SCA_OAUTH).getHref());
        }
        execution.setVariable(CONTEXT, context);
    }
}
