package de.adorsys.opba.protocol.facade.services;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.facade.config.auth.FacadeAuthConfig;
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
// FIXME - https://github.com/adorsys/open-banking-gateway/issues/555
@Service
@RequiredArgsConstructor
public class NewAuthSessionHandler {

    private final FacadeAuthConfig facadeAuthConfig;
    private final BankProtocolRepository protocolRepository;
    private final FintechUserPasswordGenerator passwordGenerator;
    private final FintechRepository fintechs;
    private final FintechUserRepository fintechUsers;
    private final FintechUserSecureStorage fintechUserVault;
    private final AuthenticationSessionRepository authenticationSessions;
    private final EntityManager entityManager;

    @NotNull
    @SneakyThrows
    @Transactional
    protected <O> AuthSession createNewAuthSession(FacadeServiceableRequest request, ServiceContext session, FacadeResultRedirectable<O, ?> result) {
        BankProtocol authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(session.getBankId(), AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + session.getBankId())
                );

        Fintech fintech = fintechs.findByGlobalId(request.getAuthorization())
                .orElseThrow(() -> new IllegalStateException("No registered FinTech: " + request.getAuthorizationSessionId()));

        // We register DUMMY user whose data will be copied after real user authorizes
        // The password of this DUMMY user is retained in url as it is safe - only if user logs in or registers
        // he will be able to see the data stored in here and only real users' password is capable to open consent
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
                new FintechUserSecureStorage.FinTechUserInboxData(result.getRedirectionTo(), session)
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
}
