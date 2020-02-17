package de.adorsys.opba.protocol.facade.services;

import com.google.crypto.tink.Aead;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.config.FacadeSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

import static de.adorsys.opba.protocol.facade.utils.EncryptionUtils.getNewSalt;

@Service
@RequiredArgsConstructor
public class SecretKeyOperationsImpl implements SecretKeyOperations {

    private final Aead aeadSystem;
    private final FacadeSecurityProvider provider;
    private final EncryptionProperties properties;

    @Override
    @SneakyThrows
    public byte[] encrypt(byte[] key) {
        byte[] encryptedPassword = aeadSystem.encrypt(key, null);
        return Base64.getEncoder().encode(encryptedPassword);
    }

    @Override
    @SneakyThrows
    public byte[] decrypt(byte[] key) {
        byte[] decoded = Base64.getDecoder().decode(key);
        return aeadSystem.decrypt(decoded, null);
    }

    @Override
    @SneakyThrows
    public byte[] generateKey(String password, String algo, byte[] salt, int iterCount) {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterCount);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(algo, provider.getProvider());
        return keyFac.generateSecret(pbeKeySpec).getEncoded();
    }

    @Override
    @SneakyThrows
    public byte[] generateKey(String password, byte[] salt) {
        return generateKey(password, properties.getAlgorithm(), salt, properties.getIterationCount());
    }

    @Override
    @SneakyThrows
    public byte[] generateKey(String password) {
        return generateKey(password, getNewSalt(properties.getSaltLength()));
    }
}
