package de.adorsys.opba.protocol.facade.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class EncryptionService {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private Aead aead;

    @SneakyThrows
    public EncryptionService() {
        AeadConfig.register();
//        KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
//        CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File("my_keyset.json")));

        String path = Paths.get(Resources.getResource("keyset.json").toURI()).toAbsolutePath().toString();
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        aead = keysetHandle.getPrimitive(Aead.class);
    }

    @SneakyThrows
    public <T> String encrypt(T request, String password) {
//        AesGcmJce aesGcmJce = new AesGcmJce(password.getBytes());
//        byte[] encryptedKey = aesGcmJce.encrypt(password.getBytes(), null);

        byte[] encrypt = aead.encrypt(MAPPER.writeValueAsBytes(request), null);
        return new String(encrypt);
    }

    @SneakyThrows
    public <T> T decrypt(String encrypted, JavaType valueType) {
        byte[] bytes = aead.decrypt(encrypted.getBytes(), null);
        return MAPPER.readValue(bytes, valueType);
    }
}
