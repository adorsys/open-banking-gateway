package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.OnLoginRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultHeaders;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRuntimeErrorResult;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth.PsuFintechAssociationService;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for PSU logging into OBG - verifying credentials and other.
 */

@SuppressWarnings("PMD.")
@Service
@RequiredArgsConstructor
public class PsuLoginService {

    private final TransactionOperations oper;
    private final OnLoginService onLoginService;
    private final EncryptionKeySerde serde;
    private final PsuRepository psus;
    private final PsuFintechAssociationService associationService;
    private final AuthorizationSessionRepository authRepository;

    /**
     * Used for the cases when PSU should be identified i.e. for consent sharing, so that PSU can manage associated entities.
     */
    public CompletableFuture<Outcome> loginInPsuScopeAndAssociateAuthSession(String psuLogin, String psuPassword, UUID authorizationId, String authorizationPassword) {
        var exchange = oper.execute(callback -> {
            AuthSession session = authRepository.findById(authorizationId)
                    .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));
            session.setPsu(psus.findByLogin(psuLogin).orElseThrow(() -> new IllegalStateException("No PSU found: " + psuLogin)));
            associationService.sharePsuAspspSecretKeyWithFintech(psuPassword, session);
            FintechConsentSpecSecureStorage.FinTechUserInboxData inbox = associationService.readInboxFromFinTech(session, authorizationPassword);
            session.setStatus(SessionStatus.STARTED);
            authRepository.save(session);
            return new SessionAndInbox(session.getRedirectCode(), inbox);
        });

        return executeOnLoginAndMap(exchange.getInbox(), authorizationId, exchange.getRedirectCode());
    }

    /**
     * Used for the cases when there is no need to identify PSU - i.e. single time payment, so that requesting FinTech can
     * manage associated entities.
     */
    public CompletableFuture<Outcome> anonymousPsuAssociateAuthSession(UUID authorizationId, String authorizationPassword) {
        var exchange = oper.execute(callback -> {
            AuthSession session = authRepository.findById(authorizationId)
                    .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));

            if (!session.isPsuAnonymous()) {
                throw new IllegalStateException("Session does not support anonymous PSU: " + authorizationId);
            }

            FintechConsentSpecSecureStorage.FinTechUserInboxData inbox = associationService.readInboxFromFinTech(session, authorizationPassword);
            session.setStatus(SessionStatus.STARTED);
            authRepository.save(session);
            return new SessionAndInbox(session.getRedirectCode(), inbox);
        });

        return executeOnLoginAndMap(exchange.getInbox(), authorizationId, exchange.getRedirectCode());
    }

    @SuppressWarnings("CPD-START")
    /**
     * Used for the cases when there is no need to identify PSU - i.e. single time payment, so that requesting FinTech can
     * manage associated entities.
     */
    @Transactional
    public CompletableFuture<OutcomeWithHeaders> anonymousPsuAssociateAuthSessionWithHeaders(UUID authorizationId, String authorizationPassword) {


        AuthSession session = authRepository.findById(authorizationId)
                .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));
            SessionAndInbox result = readInboxWithTransaction(session, authorizationPassword);

        Map<String, String> headers = createHeadersFromSession(session, result.getInbox());


        return executeOnLoginAndMapWithHeaders(result.getInbox(), authorizationId, result.getRedirectCode(),  headers);
    }
    @SuppressWarnings("CPD-END")


    private CompletableFuture<Outcome> executeOnLoginAndMap(FintechConsentSpecSecureStorage.FinTechUserInboxData association, UUID authorizationSessionId, String redirectCode) {
        return onLoginService.execute(OnLoginRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .authorizationSessionId(authorizationSessionId.toString())
                                .redirectCode(redirectCode)
                                .authorizationKey(serde.asString(association.getProtocolKey().asKey()))
                        .build()
                ).build()
        ).thenApply(it -> createResultOutcome(association, it));
    }

    private CompletableFuture<OutcomeWithHeaders> executeOnLoginAndMapWithHeaders(FintechConsentSpecSecureStorage.FinTechUserInboxData association,
                                                                                  UUID authorizationSessionId,
                                                                                  String redirectCode,
                                                                                  Map<String, String> headers) {
        return onLoginService.execute(OnLoginRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .authorizationSessionId(authorizationSessionId.toString())
                                .redirectCode(redirectCode)
                                .authorizationKey(serde.asString(association.getProtocolKey().asKey()))
                                .build()
                ).build()
        ).thenApply(it -> (OutcomeWithHeaders) createResultOutcomeWithHeaders(association, (FacadeResultHeaders<UpdateAuthBody>) it, headers));
    }

    @NotNull
    private Outcome createResultOutcome(FintechConsentSpecSecureStorage.FinTechUserInboxData association, FacadeResult<UpdateAuthBody> it) {
        if (!(it instanceof FacadeRedirectResult) && it != null) {
            if (it instanceof FacadeRuntimeErrorResult) {
                var err = (FacadeRuntimeErrorResult) it;
                return new ErrorOutcome(err.getHeaders());
            }
            return new ErrorOutcome(Collections.emptyMap());
        }
        return new Outcome(
            serde.asString(association.getProtocolKey().asKey()),
            null == it ? association.getAfterPsuIdentifiedRedirectTo() : ((FacadeRedirectResult) it).getRedirectionTo()
        );
    }

    @NonNull
    private Outcome createResultOutcomeWithHeaders(FintechConsentSpecSecureStorage.FinTechUserInboxData association, FacadeResultHeaders<UpdateAuthBody> it, Map<String, String> headers) {
        return new OutcomeWithHeaders(
                serde.asString(association.getProtocolKey().asKey()),
                null == it ? association.getAfterPsuIdentifiedRedirectTo() : ((FacadeRedirectResult) it).getRedirectionTo(),
                headers
        );
    }

    private Map<String, String> createHeadersFromSession(AuthSession session, FintechConsentSpecSecureStorage.FinTechUserInboxData inbox) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Redirect-code", session.getRedirectCode());
        headers.put("Authorization-Session-ID", session.getId().toString());
        headers.put("Set-Cookie", serde.asString(inbox.getProtocolKey().asKey()));
        // Add Location header with redirect code
        String locationUrl = inbox.getAfterPsuIdentifiedRedirectTo().toString();
        if (!locationUrl.contains("redirectCode=")) {
            locationUrl += (locationUrl.contains("?") ? "&" : "?") + "redirectCode=" + session.getRedirectCode();
        }
        headers.put("Location", locationUrl);

        return headers;
    }


    @Getter
    @RequiredArgsConstructor
    public static class Outcome {

        @NonNull
        private final String key;

        @NonNull
        private final URI redirectLocation;


    }
    @Transactional
    public SessionAndInbox readInboxWithTransaction(AuthSession session, String authorizationPassword) {
        FintechConsentSpecSecureStorage.FinTechUserInboxData inbox =
                associationService.readInboxFromFinTech(session, authorizationPassword);
        session.setStatus(SessionStatus.STARTED);

        return new SessionAndInbox(session.getRedirectCode(), inbox);
    }

    @Getter
    public static class ErrorOutcome extends Outcome {

        public ErrorOutcome(Map<String, String> headers) {
            super("", URI.create(""));
            this.headers = headers;
        }

        private final Map<String, String> headers;
    }

    @Getter
    public static class OutcomeWithHeaders extends Outcome {
        private final Map<String, String> headers;

        public OutcomeWithHeaders(@NonNull String key, @NonNull URI redirectLocation, Map<String, String> headers) {
            super(key, redirectLocation);
            this.headers = headers;
        }


    }

    @Data
    private static class SessionAndInbox {
        private final String redirectCode;
        private final FintechConsentSpecSecureStorage.FinTechUserInboxData inbox;
    }
}
