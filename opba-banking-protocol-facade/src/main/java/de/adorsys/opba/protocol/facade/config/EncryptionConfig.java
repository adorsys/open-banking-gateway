package de.adorsys.opba.protocol.facade.config;

import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

@Configuration
public class EncryptionConfig {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int SALT_LENGTH = 8;
    public static final int ITER_COUNT = 1024;
    public static final String ALGO = "PBEWithSHA256And256BitAES-CBC-BC";
    @Value("${security.provider.name}")
    String providerName;

    @Bean
    @SneakyThrows
    public Aead systemAeadConfig() {
        AeadConfig.register();
        String path = Paths.get(Resources.getResource("example-keyset.json").toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        return systemKeysetHandle.getPrimitive(Aead.class);
    }

    @Bean
    public Provider securityProvider() {
        if (null == Security.getProperty(providerName)) {
            Security.addProvider(new BouncyCastleProvider());
        }

        return Security.getProvider(providerName);
    }
}
