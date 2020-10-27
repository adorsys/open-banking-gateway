package de.adorsys.opba.protocol.facade.config.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.datasafe.business.impl.service.DaggerDefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.directory.impl.profile.operations.actions.ProfileRetrievalServiceImplRuntimeDelegatable;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.encrypiton.api.types.encryption.EncryptionConfig;
import de.adorsys.datasafe.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe.encrypiton.impl.pathencryption.PathEncryptionImpl;
import de.adorsys.datasafe.encrypiton.impl.pathencryption.PathEncryptionImplRuntimeDelegatable;
import de.adorsys.datasafe.types.api.context.BaseOverridesRegistry;
import de.adorsys.datasafe.types.api.context.overrides.OverridesRegistry;
import de.adorsys.datasafe.types.api.resource.Uri;
import de.adorsys.opba.db.repository.jpa.DatasafeConfigRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecDatasafeStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechDatasafeStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuDatasafeStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.util.function.Function;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatasafeConfigurer {

    private static final String ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX = "${" + FACADE_CONFIG_PREFIX + "encryption.datasafe.read-keystore";

    private final ObjectMapper mapper;
    private final FintechDatasafeStorage fintechStorage;
    private final PsuDatasafeStorage psuStorage;
    private final FintechConsentSpecDatasafeStorage fintechUserStorage;
    private final DatasafeConfigRepository datasafeConfigRepository;

    @Bean
    public FintechSecureStorage fintechDatasafeServices(
            @Value(ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX + ".fintech}") String fintechReadStorePass,
            EncryptionKeySerde serde,
            MutableEncryptionConfig encryptionConfig
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(fintechReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableFintechRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        return new FintechSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(enConfig)
                        .storage(fintechStorage)
                        .overridesRegistry(overridesRegistry)
                        .build(),
                config,
                serde
        );
    }

    @Bean
    public PsuSecureStorage psuDatasafeServices(
            @Value(ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX + ".psu}") String psuReadStorePass,
            PsuEncryptionServiceProvider encryptionServiceProvider,
            EncryptionKeySerde serde,
            MutableEncryptionConfig encryptionConfig
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(psuReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableUserRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        EncryptionConfig enConfig = encryptionConfig.toEncryptionConfig();
        return new PsuSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(enConfig)
                        .storage(psuStorage)
                        .overridesRegistry(overridesRegistry)
                        .build(),
                config,
                encryptionServiceProvider,
                serde
        );
    }

    @Bean
    public FintechConsentSpecSecureStorage fintechUserDatasafeServices(
            @Value(ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX + ".fintech-user}") String psuReadStorePass,
            MutableEncryptionConfig encryptionConfig
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(psuReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableUserRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        EncryptionConfig enConfig = encryptionConfig.toEncryptionConfig();
        return new FintechConsentSpecSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(enConfig)
                        .storage(fintechUserStorage)
                        .overridesRegistry(overridesRegistry)
                        .build(),
                config,
                mapper
        );
    }

    @Bean
    @ConfigurationProperties(prefix = "facade.datasafe")
    public EncryptionConfig encryptionConfig() {
        return EncryptionConfig.builder().build();
    }

    private EncryptionConfig buildEncryptionConfig() {
        if (datasafeConfigRepository.count() == 0) {
            log.info("Datasafe is configured from properties");
        }

        log.info("Datasafe is configured from database");
        return datasafeConfigRepository.findAll().stream()
                       .findFirst()
                       .map(e -> mapToEncryptionConfig(e.getConfig()))
                       .orElseGet(this::encryptionConfig.b);
    }*/

    @SneakyThrows
    private EncryptionConfig mapToEncryptionConfig(byte[] data) {
        return mapper.readValue(data, EncryptionConfig.class);
    }

    @PostConstruct
    void provideBouncyCastle() {
        if (null != Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            return;
        }

        Security.addProvider(new BouncyCastleProvider());
    }

    // Path encryption that does not encrypt paths - as for use cases of OpenBanking we need to protect data
    // not path to it.
    class NoOpPathEncryptionImplOverridden extends PathEncryptionImpl {

        NoOpPathEncryptionImplOverridden(PathEncryptionImplRuntimeDelegatable.ArgumentsCaptor captor) {
            super(captor.getSymmetricPathEncryptionService(), captor.getPrivateKeyService());
        }

        @Override
        public Uri encrypt(UserIDAuth forUser, Uri path) {
            // encryption disabled:
            return path;
        }

        @Override
        public Function<Uri, Uri> decryptor(UserIDAuth forUser) {
            // encryption disabled:
            return Function.identity();
        }
    }
}
