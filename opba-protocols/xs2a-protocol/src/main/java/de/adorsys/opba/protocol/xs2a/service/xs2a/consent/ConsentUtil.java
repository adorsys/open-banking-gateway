package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.api.ResponseHeaders.ASPSP_SCA_APPROACH;
import static de.adorsys.xs2a.adapter.impl.link.bg.template.LinksTemplate.SCA_OAUTH;

@UtilityClass
public class ConsentUtil {

    public void postHandleCreatedConsent(Response<ConsentsResponse201> consentInit, DelegateExecution execution, Xs2aContext context) {
        context.setWrongAuthCredentials(false);
        context.setConsentId(consentInit.getBody().getConsentId());
        if (null != consentInit.getBody().getLinks() && consentInit.getBody().getLinks().containsKey(SCA_OAUTH)) {
            context.setOauth2IntegratedNeeded(true);
            context.setScaOauth2Link(consentInit.getBody().getLinks().get(SCA_OAUTH).getHref());
        }

        if (null != consentInit.getHeaders() && Strings.isNotBlank(consentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH))) {
            context.setAspspScaApproach(consentInit.getHeaders().getHeader(ASPSP_SCA_APPROACH));
            if (null != consentInit.getBody()) {
                context.setConsentOrPaymentCreateLinks(consentInit.getBody().getLinks());
            }
        }

        execution.setVariable(CONTEXT, context);
    }
}
