package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.dto.KeyDto;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;

@Service
@RequiredArgsConstructor
public class FacadeEncryptionServiceFactory {

    private final Environment env;

    public EncryptionService provideEncryptionService(KeyDto key) {
        if (Arrays.asList(env.getActiveProfiles()).contains(NO_ENCRYPTION)) {
            return new NoEncryptionServiceImpl();
        }

        return new EncryptionServiceImpl(key.getKey(), key.getId());
    }
}
