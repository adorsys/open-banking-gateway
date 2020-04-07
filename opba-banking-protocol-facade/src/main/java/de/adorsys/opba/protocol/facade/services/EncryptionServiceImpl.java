package de.adorsys.opba.protocol.facade.services;

import com.google.crypto.tink.subtle.AesGcmJce;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    private final byte[] key;
    private final UUID requestId;

    @Override
    public String id() {
        return requestId.toString();
    }

    @Override
    @SneakyThrows
    public byte[] encrypt(byte[] data) {
        AesGcmJce agjEncryption = new AesGcmJce(key);
        byte[] encrypted = agjEncryption.encrypt(data, null);
        return Base64.getEncoder().encode(encrypted);
    }

    @Override
    @SneakyThrows
    public byte[] decrypt(byte[] data) {
        AesGcmJce agjDecryption = new AesGcmJce(key);
        byte[] decoded = Base64.getDecoder().decode(data);
        return agjDecryption.decrypt(decoded, null);
    }
}
