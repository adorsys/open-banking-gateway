package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

// FIXME - this should be removed
@Service
@RequiredArgsConstructor
public class DummyPsuAndFinTechCreationService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;

    private final FintechRepository fintechRepository;
    private final FintechSecureStorage fintechSecureStorage;

    @PostConstruct
    @Transactional
    public void createPsuAndFinTech() {
        Psu psu = psuRepository.save(Psu.builder().login("PSU").build());
        psuSecureStorage.registerPsu(psu, "1234"::toCharArray);

        Fintech fintech = fintechRepository.save(Fintech.builder().globalId(UUID.fromString("bff9db32-4c40-4c7f-a99b-e3cdffd377ba")).build());
        fintechSecureStorage.registerFintech(fintech, "1234"::toCharArray);
    }
}
