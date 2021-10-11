package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.OnLoginRequest;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth.PsuFintechAssociationService;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    private CompletableFuture<Outcome> executeOnLoginAndMap(FintechConsentSpecSecureStorage.FinTechUserInboxData association, UUID authorizationSessionId, String redirectCode) {
        return onLoginService.execute(OnLoginRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .authorizationSessionId(authorizationSessionId.toString())
                                .redirectCode(redirectCode)
                                .authorizationKey(serde.asString(association.getProtocolKey().asKey()))
                        .build()
                ).build()
        ).thenApply(it ->
                new Outcome(
                        serde.asString(association.getProtocolKey().asKey()),
                        null == it ? association.getAfterPsuIdentifiedRedirectTo() : ((FacadeRedirectResult) it).getRedirectionTo()
                )
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class Outcome {

        @NonNull
        private final String key;

        @NonNull
        private final URI redirectLocation;
    }

    @Data
    private static class SessionAndInbox {
        private final String redirectCode;
        private final FintechConsentSpecSecureStorage.FinTechUserInboxData inbox;
    }
}
