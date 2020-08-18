package de.adorsys.opba.fintech.impl.service.oauth2;

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
import de.adorsys.opba.fintech.impl.config.Oauth2Config;
import de.adorsys.opba.fintech.impl.database.entities.OauthSessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.OauthSessionEntityRepository;
import de.adorsys.opba.fintech.impl.exceptions.EmailNotAllowed;
import de.adorsys.opba.fintech.impl.exceptions.EmailNotVerified;
import de.adorsys.opba.fintech.impl.exceptions.Oauth2UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseOauth2AuthorizationCodeAuthenticator implements Oauth2Authenticator {

    private final FintechUiConfig fintechUiConfig;
    private final Oauth2Config oauth2Config;
    private final OauthSessionEntityRepository sessions;

    @SneakyThrows
    @Transactional
    public Oauth2AuthResult authenticateByRedirectingUserToIdp() {
        ClientID clientID = getClientID();
        State state = new State(getProvider().encode(new State().getValue()));
        Nonce nonce = new Nonce();

        AuthenticationRequest request = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
                new Scope(oauth2Config.getScope().toArray(new String[0])),
                clientID,
                fintechUiConfig.getOauth2LoginCallbackUrl()
        )
                .nonce(new Nonce())
                .state(state)
                .endpointURI(oauth2Config.getAuthenticationEndpoint())
                .nonce(nonce)
                .build();

        sessions.save(new OauthSessionEntity(state.getValue()));
        return new Oauth2AuthResult(state.getValue(), request.toURI());
    }

    @Override
    public Optional<String> authenticatedUserName(String code) {
        return Optional.ofNullable(exchangeCodeToTokenAndYieldEmail(code));
    }

    @SneakyThrows
    protected String exchangeCodeToTokenAndYieldEmail(String authCode) {
        AuthorizationCode code = new AuthorizationCode(authCode);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, fintechUiConfig.getOauth2LoginCallbackUrl());
        TokenRequest request = new TokenRequest(
                oauth2Config.getCodeToTokenEndpoint(),
                new ClientSecretBasic(getClientID(), new Secret(oauth2Config.getClientSecret())),
                codeGrant
        );

        TokenResponse tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send());
        if (!tokenResponse.indicatesSuccess()) {
            throw new Oauth2UnauthorizedException("Unable to exchange code to token: " + tokenResponse.toErrorResponse().getErrorObject().getDescription());
        }

        OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenResponse.toSuccessResponse();
        JWT idToken = successResponse.getOIDCTokens().getIDToken();
        JSONObject idJson = ((SignedJWT) idToken).getPayload().toJSONObject();
        boolean isEmailVerified = "true".equals(idJson.getAsString("email_verified"));
        String email = idJson.getAsString("email");
        if (!isEmailVerified) {
            throw new EmailNotVerified("Email not verified: " + email);
        }

        if (!checkIfEmailIsAllowed(email)) {
            throw new EmailNotAllowed("Email is not allowed: " + email);
        }

        return email;
    }

    protected boolean checkIfEmailIsAllowed(String email) {
        return oauth2Config.getAllowedEmailsRegex()
                .stream()
                .anyMatch(email::matches);
    }

    protected ClientID getClientID() {
        return new ClientID(oauth2Config.getClientId());
    }
}
