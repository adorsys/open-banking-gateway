package de.adorsys.opba.protocol.facade.config;

import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.Security;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;

// FIXME - Drop it
@Configuration
@Slf4j
public class EncryptionConfig {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Bean
    @SneakyThrows
    @Profile("!" + NO_ENCRYPTION)
    public Aead systemAeadConfig(EncryptionProperties properties) {
        AeadConfig.register();
        String keySetPath = properties.getKeySetPath();
        String path = Paths.get(keySetPath).toFile().exists()
                ? Paths.get(keySetPath).toAbsolutePath().toString()
                : Paths.get(Resources.getResource(keySetPath).toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        return systemKeysetHandle.getPrimitive(Aead.class);
    }

    @Bean
    @Profile("!" + NO_ENCRYPTION)
    public FacadeSecurityProvider securityProvider(EncryptionProperties properties) {
        String providerName = properties.getProviderName();
        if (null == Security.getProperty(providerName)) {
            Security.addProvider(new BouncyCastleProvider());
        }

        return new FacadeSecurityProvider(Security.getProvider(providerName));
    }

    @Bean
    @ConditionalOnMissingBean(Aead.class)
    public Aead systemAeadNoEncryptionConfig() {
        log.warn("[DEVELOPMENT-CONFIG] Open banking is working WITHOUT ENCRYPTION!");
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
    @ConditionalOnMissingBean(FacadeSecurityProvider.class)
    public FacadeSecurityProvider securityProviderNoEncryption() {
        return new FacadeSecurityProvider(null);
    }
}
