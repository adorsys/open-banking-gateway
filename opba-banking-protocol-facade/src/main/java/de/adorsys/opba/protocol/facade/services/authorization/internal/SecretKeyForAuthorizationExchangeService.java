package de.adorsys.opba.protocol.facade.services.authorization.internal;

import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class SecretKeyForAuthorizationExchangeService {

    private final PsuSecureStorage vault;

    public SecretKey encryptAndStoreForFuture(String psuLogin, String psuPassword, FintechUserSecureStorage.FinTechUserInboxData data) {
        vault.
    }
}
