package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import javax.annotation.PostConstruct;

// FIXME - this should be removed
@Service
@RequiredArgsConstructor
public class DummyPsuAndFinTechCreationService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;

    private final FintechRepository fintechRepository;
    private final FintechSecureStorage fintechSecureStorage;

    private final TransactionOperations txOper;

    @PostConstruct
    public void createPsuAndFinTech() {
        txOper.execute(callback -> {
            String psuId = "Anton_Brueckner";
            psuRepository.findByLogin(psuId).orElseGet(() -> {
                Psu psu = psuRepository.save(Psu.builder().login(psuId).build());
                psuSecureStorage.registerPsu(psu, "1234"::toCharArray);
                return psu;
            });

            String fintechId = "MY-SUPER-FINTECH-ID";
            fintechRepository.findByGlobalId(fintechId).orElseGet(() -> {
                Fintech fintech = fintechRepository.save(Fintech.builder().globalId(fintechId).build());
                fintechSecureStorage.registerFintech(fintech, "1234"::toCharArray);
                return fintech;
            });
            return null;
        });
    }
}
