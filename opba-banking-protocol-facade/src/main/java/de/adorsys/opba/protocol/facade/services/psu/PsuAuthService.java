package de.adorsys.opba.protocol.facade.services.psu;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.protocol.facade.exceptions.PsuDoesNotExist;
import de.adorsys.opba.protocol.facade.exceptions.PsuWrongCredentials;
import de.adorsys.opba.protocol.facade.exceptions.PsuRegisterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * PSU authentication service.
 */
@Service
@RequiredArgsConstructor
public class PsuAuthService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;

    /**
     * Try to authenticate PSU give login and password
     * @param login PSU login
     * @param password PSU password
     * @return PSU entity if user was successfully authenticated
     * @throws PsuWrongCredentials Exception indicating user has provided wrong name or password.
     */
    @Transactional
    public Psu tryAuthenticateUser(String login, String password) throws PsuWrongCredentials {
        Optional<Psu> psu = psuRepository.findByLogin(login);
        if (!psu.isPresent()) {
            throw new PsuDoesNotExist("User not found: " + login);
        }
        UserIDAuth idAuth = new UserIDAuth(psu.get().getId().toString(), password::toCharArray);
        enableDatasafeAuthentication(idAuth);
        return psu.get();
    }

    /**
     * Create new PSU if it does not exists yet.
     * @param login PSU login
     * @param password PSU password
     * @return New PSU
     */
    @Transactional
    public Psu createPsuIfNotExist(String login, String password) {
        Optional<Psu> psu = psuRepository.findByLogin(login);
        if (psu.isPresent()) {
            throw new PsuRegisterException("Psu already exists:" + login);
        }
        Psu newPsu = psuRepository.save(Psu.builder().login(login).build());
        psuSecureStorage.registerPsu(newPsu, password::toCharArray);
        return newPsu;
    }

    private void enableDatasafeAuthentication(UserIDAuth idAuth) throws PsuWrongCredentials {
        try {
            psuSecureStorage.userProfile().updateReadKeyPassword(idAuth, idAuth.getReadKeyPassword());
        } catch (Exception e) {
            throw new PsuWrongCredentials(e.getMessage(), e);
        }
    }
}
