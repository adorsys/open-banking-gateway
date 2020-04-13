package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.api.dto.consent.ConsentResult;
import de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth.PsuFintechAssociationService;
import de.adorsys.opba.protocol.facade.services.consent.ConsentSearchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PsuLoginForAisService {

    private final PsuRepository psus;
    private final PsuFintechAssociationService associationService;
    private final AuthorizationSessionRepository authRepository;
    private final ConsentSearchService searchService;

    @Transactional
    public Outcome loginAndAssociateAuthSession(String login, String password, UUID authorizationId, String authorizationPassword) {
        AuthSession session = authRepository.findById(authorizationId)
                .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));

        associationService.shareAspspSecretKeyWithFintech(password, session);
        PsuFintechAssociationService.Association association = associationService.associateAspspWithFintech(session, authorizationPassword);
        Optional<ConsentResult> consent = searchService.findConsent(association.getConsentSpec());
        session.setPsu(psus.findByLogin(login).orElseThrow(() -> new IllegalStateException("No PSU found: " + login)));
        authRepository.save(session);

        return new Outcome(
                association,
                consent.isPresent()
                        ? association.getConsentSpec().getAfterPsuIdentifiedAndConsentExistsRedirectTo()
                        : association.getConsentSpec().getAfterPsuIdentifiedAndNoConsentRedirectTo()
        );
    }

    @RequiredArgsConstructor
    public static class Outcome implements SecretKey {

        @Delegate
        private final SecretKey key;

        @Getter
        private final URI redirectLocation;
    }
}
