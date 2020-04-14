package de.adorsys.opba.protocol.facade.services.consent;

import de.adorsys.opba.protocol.api.dto.consent.ConsentResult;
import de.adorsys.opba.protocol.api.search.FindConsent;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsentSearchService {

    private final Map<String, ? extends FindConsent> matchers;

    @Transactional
    public Optional<ConsentResult> findConsent(SecretKeyWithIv psuAspspKey, FintechUserSecureStorage.FinTechUserInboxData forData) {
        return Optional.empty();
    }
}
