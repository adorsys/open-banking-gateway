package de.adorsys.opba.tppauthapi.service;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PsuAuthService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;


    public Optional<Psu> getPsu(String psuId) {
        return psuRepository.findByUserId(psuId);
    }

    @Transactional
    public Optional<Psu> createPsuIfNotExist(String id, String password) {
        Optional<Psu> psu = psuRepository.findByUserId(id);
        if (psu.isPresent()) {
            return Optional.empty();
        }
        Psu newPsu = psuRepository.save(Psu.builder().build());
        UserIDAuth idAuth = new UserIDAuth(newPsu.getId().toString(), password::toCharArray);
        psuSecureStorage.registerPsu(idAuth);
        return Optional.of(newPsu);
    }
}
