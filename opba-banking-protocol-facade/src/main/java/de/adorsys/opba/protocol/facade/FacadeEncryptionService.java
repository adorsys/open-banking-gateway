package de.adorsys.opba.protocol.facade;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacadeEncryptionService {

    public EncryptionService provideEncryptionService(byte[] key) {
        return new EncryptionServiceImpl(key);
    }
}
