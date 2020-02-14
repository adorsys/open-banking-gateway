package de.adorsys.opba.protocol.facade.config;

import com.google.common.io.Resources;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.Security;

@Configuration
@AllArgsConstructor
public class EncryptionConfig {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final EncryptionProperties properties;

    @Bean
    @SneakyThrows
    public Aead systemAeadConfig() {
        AeadConfig.register();
        String keySetPath = properties.getKeySetPath();
        String path = Paths.get(keySetPath).toFile().exists()
                ? Paths.get(keySetPath).toAbsolutePath().toString()
                : Paths.get(Resources.getResource(keySetPath).toURI()).toAbsolutePath().toString();
        KeysetHandle systemKeysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withPath(path));
        return systemKeysetHandle.getPrimitive(Aead.class);
    }

    @Bean
    public FacadeSecurityProvider securityProvider() {
        if (null == Security.getProperty(properties.getProviderName())) {
            Security.addProvider(new BouncyCastleProvider());
        }

        return new FacadeSecurityProvider((BouncyCastleProvider) Security.getProvider(properties.getProviderName()));
    }
}
