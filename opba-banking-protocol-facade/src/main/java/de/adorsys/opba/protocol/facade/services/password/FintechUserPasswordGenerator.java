package de.adorsys.opba.protocol.facade.services.password;

import de.adorsys.opba.protocol.facade.config.auth.FacadeConsentAuthConfig;
import de.adorsys.opba.protocol.facade.config.auth.PasswordGenRandomConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates passwords for FinTechs' users.
 */
@Service
@RequiredArgsConstructor
public class FintechUserPasswordGenerator {

    private final PasswordGenRandomConfig.FintechUserPasswordGenRandom passwordGenRandom;

    private final FacadeConsentAuthConfig consentAuthConfig;

    @SneakyThrows
    public String generate() {
        SecureRandom random = passwordGenRandom.getRandom();
        byte[] bytes = new byte[consentAuthConfig.getRedirect().getConsentLogin().getPassword().getByteSize()];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}
