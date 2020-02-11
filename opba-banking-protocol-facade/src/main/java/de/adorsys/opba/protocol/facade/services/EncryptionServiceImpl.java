package de.adorsys.opba.protocol.facade.services;

import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.subtle.AesGcmJce;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private Aead aeadSystem;
    private Provider provider;

    @SneakyThrows
    public EncryptionServiceImpl() {
        AeadConfig.register();
        String path = Paths.get(Resources.getResource("keyset.json").toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        aeadSystem = systemKeysetHandle.getPrimitive(Aead.class);

        if (null == Security.getProperty(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }

        provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @Override
    @SneakyThrows
    public byte[] encryptPassword(String password) {
        byte[] encryptedPassword = aeadSystem.encrypt(password.getBytes(), null);
        return Base64.getEncoder().encode(encryptedPassword);
    }

    @Override
    @SneakyThrows
    public byte[] decryptPassword(byte[] encryptedPassword) {
        byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
        return aeadSystem.decrypt(decoded, null);
    }

    @Override
    @SneakyThrows
    public byte[] encrypt(byte[] data, String password) {
        SecretKey key = deriveKey(password);
        AesGcmJce agjEncryption = new AesGcmJce(key.getEncoded());
        byte[] encrypted = agjEncryption.encrypt(data, null);
        return Base64.getEncoder().encode(encrypted);
    }

    @Override
    @SneakyThrows
    public byte[] decrypt(byte[] encrypted, String password) {
        SecretKey key = deriveKey(password);
        AesGcmJce agjDecryption = new AesGcmJce(key.getEncoded());
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        return agjDecryption.decrypt(decoded, null);
    }

    @SneakyThrows
    private SecretKey deriveKey(String password) {
        byte[] salt = new byte[8];
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1024);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC", provider);
        return keyFac.generateSecret(pbeKeySpec);
    }
}
