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
import de.adorsys.opba.db.domain.entity.DatasafeConfig;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.util.function.Function;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatasafeConfigurer {

    private static final String ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX = "${" + FACADE_CONFIG_PREFIX + "encryption.datasafe.read-keystore";
    private static final String INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION = "There should be only one datasafe encryption configuration in database!";
    private static final Long ENCRYPTION_DATASAFE_CONFIG_DB_ID = 1L;

    private final ObjectMapper mapper;
    private final FintechDatasafeStorage fintechStorage;
    private final PsuDatasafeStorage psuStorage;
    private final FintechConsentSpecDatasafeStorage fintechUserStorage;
    private final DatasafeConfigRepository datasafeConfigRepository;
    private final MutableEncryptionConfig mutableEncryptionConfig;
    private final TransactionTemplate transactionTemplate;

    @Bean
    public FintechSecureStorage fintechDatasafeServices(
            @Value(ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX + ".fintech}") String fintechReadStorePass,
            EncryptionKeySerde serde
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(fintechReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableFintechRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        return new FintechSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(readEncryptionConfigFromDb())
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
            EncryptionKeySerde serde
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(psuReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableUserRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        return new PsuSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(readEncryptionConfigFromDb())
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
            @Value(ENCRYPTION_DATASAFE_READ_KEYSTORE_PREFIX + ".fintech-user}") String psuReadStorePass
    ) {
        DFSConfig config = new BaseDatasafeDbStorageService.DbTableDFSConfig(psuReadStorePass);
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, BaseDatasafeDbStorageService.DbTableUserRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        return new FintechConsentSpecSecureStorage(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .encryption(readEncryptionConfigFromDb())
                        .storage(fintechUserStorage)
                        .overridesRegistry(overridesRegistry)
                        .build(),
                config,
                mapper
        );
    }

    @PostConstruct
    void provideBouncyCastle() {
        if (null != Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            return;
        }

        Security.addProvider(new BouncyCastleProvider());

        transactionTemplate.execute(status -> {
            if (encryptionConfigNotExistInDb()) {
                log.info("Datasafe encryption is configured from properties");
                storeEncryptionConfigInDb(mutableEncryptionConfig);
            } else {
                log.info("Datasafe encryption is configured from database");
            }
            return null;
        });
    }

    @SneakyThrows
    private void storeEncryptionConfigInDb(MutableEncryptionConfig config) {
        datasafeConfigRepository.save(new DatasafeConfig(ENCRYPTION_DATASAFE_CONFIG_DB_ID, mapper.writeValueAsString(config)));
    }

    private boolean encryptionConfigNotExistInDb() {
        return datasafeConfigRepository.count() == 0;
    }

    @SneakyThrows
    private EncryptionConfig readEncryptionConfigFromDb() {
        if (datasafeConfigRepository.count() != 1) {
            throw new IllegalStateException(INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION);
        }

        return mapper.readValue(
                datasafeConfigRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION))
                        .getConfig(),
                MutableEncryptionConfig.class)
                       .toEncryptionConfig();
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
