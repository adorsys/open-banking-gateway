package de.adorsys.opba.fintech.impl.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.config.GmailOauth2Config;
import de.adorsys.opba.fintech.impl.config.Oauth2Provider;
import de.adorsys.opba.fintech.impl.database.entities.OauthSessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.OauthSessionEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
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
    public URI authenticateByRedirectingUserToIdp() {
        ClientID clientID = getClientID();
        State state = new State(GMAIL.encode(new State().getValue()));
        Nonce nonce = new Nonce();

        AuthenticationRequest request = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
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

    private ClientID getClientID() {
        return new ClientID(gmailOauth2Config.getClientId());
    }

    @Override
    public Optional<String> authenticatedUserName(String code) {
        return Optional.ofNullable(exchangeCodeToTokenAndYieldEmail(code));
    }

    @Override
    public Oauth2Provider getProvider() {
        return GMAIL;
    }

    @SneakyThrows
    private String exchangeCodeToTokenAndYieldEmail(String authCode) {
        AuthorizationCode code = new AuthorizationCode(authCode);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, fintechUiConfig.getOauth2LoginCallbackUrl());
        TokenRequest request = new TokenRequest(
                gmailOauth2Config.getCodeToTokenEndpoint(),
                new ClientSecretBasic(getClientID(), new Secret(gmailOauth2Config.getClientSecret())),
                codeGrant
        );

        TokenResponse tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send());
        if (!tokenResponse.indicatesSuccess()) {
            throw new IllegalStateException("Unable to exchange code to token: " + tokenResponse.toErrorResponse().getErrorObject().getDescription());
        }

        OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenResponse.toSuccessResponse();
        JWT idToken = successResponse.getOIDCTokens().getIDToken();
        JSONObject idJson = ((SignedJWT) idToken).getPayload().toJSONObject();
        boolean isEmailVerified = "true".equals(idJson.getAsString("email_verified"));
        if (!isEmailVerified) {
            throw new IllegalStateException("Email not verified");
        }

        return idJson.getAsString("email");
    }
}
