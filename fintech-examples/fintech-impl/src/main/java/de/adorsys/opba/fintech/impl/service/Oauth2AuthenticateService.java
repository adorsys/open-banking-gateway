package de.adorsys.opba.fintech.impl.service;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.config.GmailOauth2Config;
import de.adorsys.opba.fintech.impl.database.entities.OauthSessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.OauthSessionEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Oauth2AuthenticateService {

    private final FintechUiConfig fintechUiConfig;
    private final GmailOauth2Config gmailOauth2Config;
    private final OauthSessionEntityRepository sessions;

    @SneakyThrows
    @Transactional
    public URI authenticateByRedirectingTo() {
        ClientID clientID = new ClientID(UUID.randomUUID().toString());
        Scope scope = new Scope(gmailOauth2Config.getScope());

        State state = new State();
        AuthorizationRequest request = new AuthorizationRequest.Builder(
                new ResponseType(ResponseType.Value.TOKEN), clientID)
                .scope(scope)
                .state(state)
                .redirectionURI(fintechUiConfig.getOauth2LoginCallbackUrl())
                .endpointURI(gmailOauth2Config.getAuthenticationEndpoint())
                .build();

        sessions.save(new OauthSessionEntity(clientID.getValue(), state.getValue()));
        return request.toURI();
    }
}
