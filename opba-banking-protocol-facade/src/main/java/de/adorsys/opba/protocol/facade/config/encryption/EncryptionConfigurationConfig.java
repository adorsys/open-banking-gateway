package de.adorsys.opba.protocol.facade.config.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.datasafe.encrypiton.api.types.encryption.EncryptionConfig;
import de.adorsys.datasafe.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.opba.db.domain.entity.DatasafeConfig;
import de.adorsys.opba.db.repository.jpa.DatasafeConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EncryptionConfigurationConfig {

    private static final String INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION = "There should be only one Datasafe encryption configuration in database!";

    private final ObjectMapper mapper;
    private final DatasafeConfigRepository datasafeConfigRepository;

    @Bean
    @ConfigurationProperties(prefix = "facade.datasafe")
    public MutableEncryptionConfig mutableEncryptionConfig() {
        return new MutableEncryptionConfig();
    }

    @Bean
    @SneakyThrows
    @Transactional
    public EncryptionConfig encryptionConfig(MutableEncryptionConfig config) {
        long dbConfigCount = datasafeConfigRepository.count();
        if (dbConfigCount == 1) {
            return mapper.readValue(
                    datasafeConfigRepository.findAll().stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION))
                            .getConfig(),
                    MutableEncryptionConfig.class)
                    .toEncryptionConfig();
        } else if (dbConfigCount == 0) {
            storeEncryptionConfigInDb(config);
            return config.toEncryptionConfig();
        }

        throw new IllegalStateException(INCORRECT_ENCRYPTION_CONFIG_RECORDS_AMOUNT_EXCEPTION);
    }

    @SneakyThrows
    private void storeEncryptionConfigInDb(MutableEncryptionConfig config) {
        datasafeConfigRepository.save(DatasafeConfig.builder().config(mapper.writeValueAsString(config)).build());
    }
}
