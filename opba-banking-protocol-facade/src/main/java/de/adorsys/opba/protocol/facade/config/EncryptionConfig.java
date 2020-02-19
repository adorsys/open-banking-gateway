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
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.Security;

@Configuration
public class EncryptionConfig {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    @Value("${facade.encryption.providerName:BC}") String providerName;
    @Value("${facade.encryption.keySetPath:example-keyset.json}") String keySetPath;

    @Bean
    @SneakyThrows
    @Profile("!no-enc")
    public Aead systemAeadConfig() {
        AeadConfig.register();
        String path = Paths.get(keySetPath).toFile().exists()
                ? Paths.get(keySetPath).toAbsolutePath().toString()
                : Paths.get(Resources.getResource(keySetPath).toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        return systemKeysetHandle.getPrimitive(Aead.class);
    }

    @Bean
    @Profile("!no-enc")
    public FacadeSecurityProvider securityProvider() {
        if (null == Security.getProperty(providerName)) {
            Security.addProvider(new BouncyCastleProvider());
        }

        return new FacadeSecurityProvider((BouncyCastleProvider) Security.getProvider(providerName));
    }

    @Bean
    @Profile("no-enc")
    public Aead systemAeadNoEncryptionConfig() {
        return new Aead() {
            @Override
            public byte[] encrypt(byte[] plaintext, byte[] associatedData) {
                return plaintext;
            }

            @Override
            public byte[] decrypt(byte[] ciphertext, byte[] associatedData) {
                return ciphertext;
            }
        };
    }

    @Bean
    @Profile("no-enc")
    public FacadeSecurityProvider securityProviderNoEncryption() {
        return new FacadeSecurityProvider(null);
    }
}
