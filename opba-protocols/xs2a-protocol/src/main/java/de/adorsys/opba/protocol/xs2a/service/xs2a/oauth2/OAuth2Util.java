package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.model.HrefType;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.OAUTH_CONSENT;
import static de.adorsys.xs2a.adapter.impl.link.bg.template.LinksTemplate.SCA_OAUTH;

@UtilityClass
public class OAuth2Util {

    public void handlePossibleOAuth2(Map<String, HrefType> bodyLinks, Xs2aContext context) {
        if (bodyLinks.containsKey(SCA_OAUTH)) {
            context.setOauth2IntegratedNeeded(true);
            context.setScaOauth2Link(bodyLinks.get(SCA_OAUTH).getHref());
        } else if (bodyLinks.containsKey(OAUTH_CONSENT)) {
            context.setOauth2IntegratedNeeded(true);
            context.setOauth2ConsentNeeded(true);
        }
    }
}
