package de.adorsys.opba.protocol.facade.services.password;

import com.google.crypto.tink.subtle.Base64;
import de.adorsys.opba.protocol.facade.config.auth.FacadeAuthConfig;
import de.adorsys.opba.protocol.facade.config.auth.PasswordGenRandomConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class FintechUserPasswordGenerator {

    private final PasswordGenRandomConfig.FintechUserPasswordGenRandom passwordGenRandom;

    private final FacadeAuthConfig facadeAuthConfig;

    @SneakyThrows
    public String generate() {
        SecureRandom random = passwordGenRandom.getRandom();
        byte[] bytes = new byte[facadeAuthConfig.getRedirect().getPassword().getByteSize()];
        random.nextBytes(bytes);
        return Base64.urlSafeEncode(bytes);
    }
}
