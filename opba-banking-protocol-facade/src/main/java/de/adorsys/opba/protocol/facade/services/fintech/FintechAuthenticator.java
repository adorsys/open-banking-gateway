package de.adorsys.opba.protocol.facade.services.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.fintech.registrar.FintechRegistrar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class FintechAuthenticator {

    private final FintechRegistrar fintechRegistrar;
    private final FintechSecureStorage fintechSecureStorage;
    private final FintechRepository fintechRepository;

    @Transactional
    public Fintech authenticateOrCreateFintech(FacadeServiceableRequest request) {
        String fintechId = request.getAuthorization();
        Supplier<char[]> finTechPassword = () -> request.getSessionPassword().toCharArray();
        Fintech fintech = fintechRepository.findByGlobalId(fintechId)
                .orElseGet(() -> fintechRegistrar.registerFintech(fintechId, finTechPassword));
        fintechSecureStorage.validatePassword(fintech, finTechPassword);
        return fintech;
    }
}
