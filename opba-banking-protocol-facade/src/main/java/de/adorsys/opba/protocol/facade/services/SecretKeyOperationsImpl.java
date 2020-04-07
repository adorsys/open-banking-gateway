package de.adorsys.opba.protocol.facade.services;

import com.google.common.hash.Hashing;
import com.google.crypto.tink.Aead;
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.config.FacadeSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;
import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.SECURE_RANDOM;

@Service
@RequiredArgsConstructor
@Profile("!" + NO_ENCRYPTION)
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
    public KeyWithParamsDto generateKey(String password, String algo, byte[] salt, int iterCount) {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterCount);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(algo, provider.getProvider());
        byte[] key = keyFac.generateSecret(pbeKeySpec).getEncoded();
        return new KeyWithParamsDto(
                Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString(),
                key,
                salt,
                algo,
                properties.getSaltLength(),
                iterCount
        );
    }

    @Override
    @SneakyThrows
    public KeyWithParamsDto generateKey(String password) {
        byte[] salt = getNewSalt(properties.getSaltLength());
        return generateKey(password, properties.getAlgorithm(), salt, properties.getIterationCount());
    }

    private byte[] getNewSalt(int saltLength) {
        byte[] salt = new byte[saltLength];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
