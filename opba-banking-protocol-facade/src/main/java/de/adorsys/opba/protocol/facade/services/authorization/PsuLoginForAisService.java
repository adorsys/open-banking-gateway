package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth.PsuFintechAssociationService;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PsuLoginForAisService {

    private final EncryptionKeySerde serde;
    private final PsuRepository psus;
    private final PsuFintechAssociationService associationService;
    private final AuthorizationSessionRepository authRepository;

    @Transactional
    public Outcome loginAndAssociateAuthSession(String login, String password, UUID authorizationId, String authorizationPassword) {
        AuthSession session = authRepository.findById(authorizationId)
                .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));
        session.setPsu(psus.findByLogin(login).orElseThrow(() -> new IllegalStateException("No PSU found: " + login)));
        associationService.sharePsuAspspSecretKeyWithFintech(password, session);
        FintechConsentSpecSecureStorage.FinTechUserInboxData association = associationService.associatePsuAspspWithFintechUser(session, authorizationPassword);
        authRepository.save(session);

        return new Outcome(
                serde.asString(association.getProtocolKey().asKey()),
                association.getAfterPsuIdentifiedRedirectTo()
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
}
