package de.adorsys.opba.protocol.facade.services;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.BankProtocol;
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
import de.adorsys.opba.protocol.facade.config.auth.FacadeAuthConfig;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.services.password.FintechUserPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.AUTHORIZATION;
import static de.adorsys.opba.protocol.facade.config.auth.UriExpandConst.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.protocol.facade.config.auth.UriExpandConst.FINTECH_USER_TEMP_PASSWORD;

// FIXME - this class needs refactoring - some other class should handle FinTech user registration
@Service
@RequiredArgsConstructor
public class NewAuthSessionHandler {

    private final ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider;
    private final FacadeAuthConfig facadeAuthConfig;
    private final BankProtocolRepository protocolRepository;
    private final FintechUserPasswordGenerator passwordGenerator;
    private final FintechRepository fintechs;
    private final FintechUserRepository fintechUsers;
    private final FintechUserSecureStorage fintechUserVault;
    private final AuthorizationSessionRepository authenticationSessions;
    private final EntityManager entityManager;

    @NotNull
    @SneakyThrows
    @Transactional
    @SuppressWarnings("checkstyle:MethodLength") //  FIXME - https://github.com/adorsys/open-banking-gateway/issues/555
    protected <O> AuthSession createNewAuthSession(FacadeServiceableRequest request, ServiceContext session, FacadeResultRedirectable<O, ?> result) {
        BankProtocol authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(session.getBankId(), AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + session.getBankId())
                );

        Fintech fintech = fintechs.findByGlobalId(request.getAuthorization())
                .orElseThrow(() -> new IllegalStateException("No registered FinTech: " + request.getAuthorizationSessionId()));

        String newPassword = passwordGenerator.generate();
        FintechUser user = fintechUsers.findByPsuFintechIdAndFintech(request.getFintechUserId(), fintech)
                .orElseGet(() -> {
                    FintechUser newUser = fintechUsers.save(FintechUser.builder().psuFintechId(request.getFintechUserId()).fintech(fintech).build());
                    fintechUserVault.registerFintechUser(newUser, newPassword::toCharArray);
                    return newUser;
                });

        AuthSession newAuth = authenticationSessions.save(
                AuthSession.builder()
                        .parent(entityManager.find(ServiceSession.class, session.getServiceSessionId()))
                        .protocol(authProtocol)
                        .fintechUser(user)
                        .redirectCode(session.getFutureRedirectCode().toString())
                        .build()
        );

        fintechUserVault.toInboxForAuth(
                newAuth,
                new FintechUserSecureStorage.FinTechUserInboxData(
                        result.getRedirectionTo(),
                        createSecretKeyOfCurrentSessionContainer(session),
                        null
                )
        );
        result.setRedirectionTo(
                UriComponentsBuilder
                        .fromHttpUrl(facadeAuthConfig.getRedirect().getLoginPage())
                        .buildAndExpand(ImmutableMap.of(
                                FINTECH_USER_TEMP_PASSWORD, newPassword,
                                AUTHORIZATION_SESSION_ID, newAuth.getId()
                        )).toUri()
        );

        return newAuth;
    }

    @NotNull
    private SecretKeySerde.SecretKeyWithIvContainer createSecretKeyOfCurrentSessionContainer(ServiceContext session) {
        return new SecretKeySerde.SecretKeyWithIvContainer(encryptionServiceProvider.getEncryptionById(session.getEncryption().getId()).getKey());
    }
}
