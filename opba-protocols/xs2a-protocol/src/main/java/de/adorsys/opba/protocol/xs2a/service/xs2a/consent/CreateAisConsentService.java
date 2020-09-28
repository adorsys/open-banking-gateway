package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.quirks.QuirkUtil;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.impl.link.bg.template.LinksTemplate.SCA_OAUTH;

/**
 * Calls Xs2a API to initiate AIS consent.
 */
@Service
public class CreateAisConsentService {
    void createConsent(
            AccountInformationService ais,
            DelegateExecution execution,
            Xs2aAisContext context,
            ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, ConsentInitiateHeaders, Consents> params) {
        Response<ConsentsResponse201> consentInit = ais.createConsent(
                QuirkUtil.pushBicToXs2aAdapterHeaders(context, params.getHeaders().toHeaders()),
                params.getPath().toParameters(),
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
