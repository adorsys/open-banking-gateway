package de.adorsys.opba.protocol.facade.services;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.facade.config.auth.FacadeAuthConfig;
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

// FIXME - this class needs refactoring - some other class should handle FinTech user registration
@Service
@RequiredArgsConstructor
public class AuthSessionHandler {

    private final FacadeAuthConfig facadeAuthConfig;
    private final BankProtocolRepository protocolRepository;
    private final FintechUserPasswordGenerator passwordGenerator;
    private final FintechRepository fintechs;
    private final FintechUserRepository fintechUsers;
    private final FintechConsentSpecSecureStorage fintechUserVault;
    private final AuthorizationSessionRepository authenticationSessions;
    private final EntityManager entityManager;

    @NotNull
    @SneakyThrows
    @Transactional
    @SuppressWarnings("checkstyle:MethodLength") //  FIXME - https://github.com/adorsys/open-banking-gateway/issues/555
    public <O> AuthSession createNewAuthSessionAndEnhanceResult(
            FacadeServiceableRequest request,
            SecretKeyWithIv sessionKey,
            ServiceContext context,
            FacadeResultRedirectable<O, ?> result
    ) {
        return fillAuthSessionData(request, context, sessionKey, result);
    }

    @NotNull
    @SneakyThrows
    @Transactional
    @SuppressWarnings("checkstyle:MethodLength") //  FIXME - https://github.com/adorsys/open-banking-gateway/issues/555
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
        BankAction authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(context.getBankId(), AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + context.getBankId())
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
                        .action(authProtocol)
                        .fintechUser(user)
                        .redirectCode(context.getFutureRedirectCode().toString())
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
        String url = context.getRequest() instanceof InitiateSinglePaymentRequest
                ? facadeAuthConfig.getRedirect().getConsentLogin().getPage().getForPis()
                : facadeAuthConfig.getRedirect().getConsentLogin().getPage().getForAis();
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
