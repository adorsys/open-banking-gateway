package de.adorsys.opba.fintech.impl.service.oauth2;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.config.GmailOauth2Config;
import de.adorsys.opba.fintech.impl.config.Oauth2Provider;
import de.adorsys.opba.fintech.impl.database.repositories.OauthSessionEntityRepository;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.fintech.impl.config.Oauth2Provider.GMAIL;

@Service
public class GmailOauth2AuthenticateService extends BaseOauth2AuthorizationCodeAuthenticator {

    public GmailOauth2AuthenticateService(
            FintechUiConfig fintechUiConfig,
            GmailOauth2Config oauth2Config,
            OauthSessionEntityRepository sessions
    ) {
        super(fintechUiConfig, oauth2Config, sessions);
    }

    @Override
    public Oauth2Provider getProvider() {
        return GMAIL;
    }
}
