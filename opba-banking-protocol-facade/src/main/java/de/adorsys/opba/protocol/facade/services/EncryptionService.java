package de.adorsys.opba.protocol.facade.services;

import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.subtle.AesGcmJce;
import de.adorsys.keymanagement.api.types.template.DefaultNamingStrategy;
import de.adorsys.keymanagement.api.types.template.NameAndPassword;
import de.adorsys.keymanagement.api.types.template.generated.Pbe;
import de.adorsys.keymanagement.api.types.template.generated.PbeKeyEncryptionTemplate;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.juggler.services.BCJuggler;
import de.adorsys.keymanagement.juggler.services.DaggerBCJuggler;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Base64;

@Service
public class EncryptionService {

    private Aead aeadSystem;

    @SneakyThrows
    public EncryptionService() {
        AeadConfig.register();
        String path = Paths.get(Resources.getResource("keyset.json").toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        aeadSystem = systemKeysetHandle.getPrimitive(Aead.class);
    }

    @SneakyThrows
    public byte[] encryptPassword(byte[] key) {
        byte[] encryptedPassword = aeadSystem.encrypt(key, null);
        return Base64.getEncoder().encode(encryptedPassword);
    }

    @SneakyThrows
    public byte[] decryptPassword(byte[] encryptedPassword) {
        byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
        return aeadSystem.decrypt(decoded, null);
    }

    @SneakyThrows
    public String encrypt(byte[] data, byte[] key) {
        AesGcmJce agjEncryption = new AesGcmJce(key);
        byte[] encrypted = agjEncryption.encrypt(data, null);
        return new String(encrypted);
    }

    @SneakyThrows
    public byte[] decrypt(byte[] encrypted, byte[] key) {
        AesGcmJce agjDecryption = new AesGcmJce(key);
        return agjDecryption.decrypt(encrypted, null);
    }

    public ProvidedKey deriveKey(String password) {
        BCJuggler juggler = DaggerBCJuggler.builder().build();
        return juggler.generateKeys().secret(
                Pbe.builder()
                        .data(password.toCharArray())
                        .encryptionTemplate(PbeKeyEncryptionTemplate.builder().algo("PBEWithSHA256And256BitAES-CBC-BC").saltLen(8).iterCount(1024).build())
                        .keyTemplate(new NameAndPassword(new DefaultNamingStrategy("alias", null), password::toCharArray))
                        .build()
        );
    }
}
