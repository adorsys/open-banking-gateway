package de.adorsys.opba.protocol.facade.services.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.fintech.registrar.FintechRegistrar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class FintechAuthenticator {

    private final FintechRegistrar fintechRegistrar;
    private final FintechSecureStorage fintechSecureStorage;
    private final FintechRepository fintechRepository;

    @Transactional
    public Fintech authenticateOrCreateFintech(FacadeServiceableRequest request, ServiceSession session) {
        String fintechId = request.getAuthorization();
        if (null != session.getAuthSession() && null != session.getAuthSession().getFintechUser() && !session.getAuthSession().getFintechUser().getFintech().getGlobalId().equals(fintechId)) {
            log.error("[SECURITY] Fintech [{}] has requested data belonging to [{}] fintech", fintechId, session.getAuthSession().getFintechUser().getFintech().getGlobalId());
            throw new IllegalStateException("Security violation");
        }

        Supplier<char[]> finTechPassword = () -> request.getSessionPassword().toCharArray();
        Fintech fintech = fintechRepository.findByGlobalId(fintechId)
                .orElseGet(() -> fintechRegistrar.registerFintech(fintechId, finTechPassword));
        fintechSecureStorage.validatePassword(fintech, finTechPassword);
        return fintech;
    }
}
