package de.adorsys.opba.protocol.facade.services.psu;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.protocol.facade.exceptions.PsuAuthenticationException;
import de.adorsys.opba.protocol.facade.exceptions.PsuAuthorizationException;
import de.adorsys.opba.protocol.facade.exceptions.PsuRegisterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PsuAuthService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;

    @Transactional
    public Psu tryAuthenticateUser(String login, String password) throws PsuAuthorizationException {
        Optional<Psu> psu = psuRepository.findByLogin(login);
        if (!psu.isPresent()) {
            throw new PsuAuthenticationException("User not found: " + login);
        }
        UserIDAuth idAuth = new UserIDAuth(psu.get().getId().toString(), password::toCharArray);
        enableDatasafeAuthentication(idAuth);
        return psu.get();
    }

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

    private void enableDatasafeAuthentication(UserIDAuth idAuth) throws PsuAuthorizationException {
        try {
            psuSecureStorage.userProfile().updateReadKeyPassword(idAuth, idAuth.getReadKeyPassword());
        } catch (Exception e) {
            throw new PsuAuthorizationException(e.getMessage(), e);
        }
    }
}
