package de.adorsys.opba.fintech.impl.service;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.config.GmailOauth2Config;
import de.adorsys.opba.fintech.impl.config.Oauth2Provider;
import de.adorsys.opba.fintech.impl.database.entities.OauthSessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.OauthSessionEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

import static de.adorsys.opba.fintech.impl.config.Oauth2Provider.GMAIL;

@Service
@RequiredArgsConstructor
public class GmailOauth2AuthenticateService implements Oauth2Authenticator {

    private final FintechUiConfig fintechUiConfig;
    private final GmailOauth2Config gmailOauth2Config;
    private final OauthSessionEntityRepository sessions;

    @SneakyThrows
    @Transactional
    public URI authenticateByRedirectingTo() {
        ClientID clientID = new ClientID(gmailOauth2Config.getClientId());
        State state = new State(GMAIL.encode(new State().getValue()));
        Nonce nonce = new Nonce();

        AuthenticationRequest request = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.TOKEN),
                new Scope(gmailOauth2Config.getScope()),
                clientID,
                fintechUiConfig.getOauth2LoginCallbackUrl()
        )
                .nonce(new Nonce())
                .state(state)
                .endpointURI(gmailOauth2Config.getAuthenticationEndpoint())
                .nonce(nonce)
                .build();

        sessions.save(new OauthSessionEntity(state.getValue()));
        return request.toURI();
    }

    @Override
    public Optional<String> authenticatedUserName(String code) {
        return Optional.empty();
    }

    @Override
    public Oauth2Provider getProvider() {
        return GMAIL;
    }
}
