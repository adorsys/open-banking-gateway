package de.adorsys.opba.protocol.facade.services;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankActionRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.facade.config.auth.FacadeConsentAuthConfig;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.services.password.FintechUserPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.AUTHORIZATION;
import static de.adorsys.opba.protocol.facade.config.auth.UriExpandConst.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.protocol.facade.config.auth.UriExpandConst.FINTECH_USER_TEMP_PASSWORD;

/**
 * Handles authorization session creation and continuation.
 */
// FIXME - this class needs refactoring - some other class should handle FinTech user registration
@Service
@RequiredArgsConstructor
public class AuthSessionHandler {

    private final FacadeConsentAuthConfig consentAuthConfig;
    private final BankActionRepository bankActionRepository;
    private final FintechUserPasswordGenerator passwordGenerator;
    private final FintechRepository fintechs;
    private final FintechUserRepository fintechUsers;
    private final FintechConsentSpecSecureStorage fintechUserVault;
    private final AuthorizationSessionRepository authenticationSessions;
    private final EntityManager entityManager;

    /**
     * Creates new authorization session associated with the request.
     * @param request Request to associate session with.
     * @param sessionKey Authorization session encryption key.
     * @param context Service context for the request
     * @param result Protocol response that required to open the session
     * @param <O> Outcome class
     * @return New authorization session
     */
    @NotNull
    @SneakyThrows
    @Transactional
    public <O> AuthSession createNewAuthSessionAndEnhanceResult(
            FacadeServiceableRequest request,
            SecretKeyWithIv sessionKey,
            ServiceContext context,
            FacadeResultRedirectable<O, ?> result
    ) {
        return fillAuthSessionData(request, context, sessionKey, result);
    }

    /**
     * Continues already existing authorization session associated with the request.
     * @param authSession Authorization session to continue
     * @param sessionKey Encryption key for the authorization session
     * @param context Service context for the request
     * @param result Protocol response that required to continue the session
     * @param <O> Outcome class
     * @return Authorization session to reuse
     */
    @NotNull
    @SneakyThrows
    @Transactional
    public <O> AuthSession reuseAuthSessionAndEnhanceResult(
            AuthSession authSession,
            SecretKeyWithIv sessionKey,
            ServiceContext context,
            FacadeResultRedirectable<O, ?> result) {
        return fillAuthSessionData(authSession, context, sessionKey, result);
    }

    private <O> AuthSession fillAuthSessionData(
            FacadeServiceableRequest request,
            ServiceContext context,
            SecretKeyWithIv sessionKey,
            FacadeResultRedirectable<O, ?> result
    ) {
        BankAction authAction = bankActionRepository
                .findByBankProfileUuidAndAction(context.getBankProfileId(), AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + context.getBankProfileId())
                );
        Fintech fintech = fintechs.findByGlobalId(request.getAuthorization())
                .orElseThrow(() -> new IllegalStateException("No registered FinTech: " + request.getAuthorization()));

        String newPassword = passwordGenerator.generate();
        // Always create new user entity, as this is more like authorization dummy user.
        FintechUser user = fintechUsers.save(
                FintechUser.builder()
                        .psuFintechId(request.getFintechUserId())
                        .fintech(fintech)
                        .build()
        );
        fintechUserVault.registerFintechUser(user, newPassword::toCharArray);

        AuthSession session = authenticationSessions.save(
                AuthSession.builder()
                        .parent(entityManager.find(ServiceSession.class, context.getServiceSessionId()))
                        .action(authAction)
                        .fintechUser(user)
                        .psuAnonymous(request.isAnonymousPsu())
                        .redirectCode(context.getFutureRedirectCode().toString())
                        .status(SessionStatus.PENDING)
                        .build()
        );

        return createInboxDataAndUpdateAuthSession(context, sessionKey, result, session, newPassword);
    }

    private <O> AuthSession fillAuthSessionData(
            AuthSession authSession,
            ServiceContext context,
            SecretKeyWithIv sessionKey,
            FacadeResultRedirectable<O, ?> result
    ) {
        AuthSession session = authSession;
        Fintech fintech = authSession.getFintechUser().getFintech();

        String newPassword = passwordGenerator.generate();
        // Always create new user entity, as this is more like authorization dummy user.
        FintechUser oldUser = authSession.getFintechUser();
        fintechUsers.delete(oldUser);
        FintechUser user = fintechUsers.save(
                FintechUser.builder()
                        .psuFintechId(oldUser.getPsuFintechId())
                        .fintech(fintech)
                        .build()
        );
        fintechUserVault.registerFintechUser(user, newPassword::toCharArray);
        session.setFintechUser(user);
        session.setRedirectCode(context.getFutureRedirectCode().toString());
        session = authenticationSessions.save(session);

        return createInboxDataAndUpdateAuthSession(context, sessionKey, result, session, newPassword);
    }

    @NotNull
    private <O> AuthSession createInboxDataAndUpdateAuthSession(
            ServiceContext context,
            SecretKeyWithIv sessionKey,
            FacadeResultRedirectable<O, ?> result,
            AuthSession session,
            String newPassword
    ) {
        fintechUserVault.toInboxForAuth(
                session,
                new FintechConsentSpecSecureStorage.FinTechUserInboxData(
                        result.getRedirectionTo(),
                        new EncryptionKeySerde.SecretKeyWithIvContainer(sessionKey),
                        null
                )
        );

        if (!(context.getRequest() instanceof FacadeServiceableGetter)) {
            throw new IllegalStateException("Wrong request type: " + context.getRequest());
        }
        FacadeServiceableRequest request = ((FacadeServiceableGetter) context.getRequest()).getFacadeServiceable();

        String url = request.isAnonymousPsu()
                ? consentAuthConfig.getRedirect().getConsentLogin().getPage().getForAisAnonymous()
                : consentAuthConfig.getRedirect().getConsentLogin().getPage().getForAis();

        if (context.getRequest() instanceof InitiateSinglePaymentRequest) {
            url = request.isAnonymousPsu()
                    ? consentAuthConfig.getRedirect().getConsentLogin().getPage().getForPisAnonymous()
                    : consentAuthConfig.getRedirect().getConsentLogin().getPage().getForPis();
        }

        result.setRedirectionTo(
                UriComponentsBuilder
                        .fromHttpUrl(url)
                        .buildAndExpand(ImmutableMap.of(
                                FINTECH_USER_TEMP_PASSWORD, newPassword,
                                AUTHORIZATION_SESSION_ID, session.getId()
                        )).toUri()
        );

        return session;
    }
}
