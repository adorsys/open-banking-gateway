package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.datasafe.business.impl.service.DaggerDefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.directory.impl.profile.operations.actions.ProfileRetrievalServiceImplRuntimeDelegatable;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.encrypiton.impl.pathencryption.PathEncryptionImpl;
import de.adorsys.datasafe.encrypiton.impl.pathencryption.PathEncryptionImplRuntimeDelegatable;
import de.adorsys.datasafe.types.api.context.BaseOverridesRegistry;
import de.adorsys.datasafe.types.api.context.overrides.OverridesRegistry;
import de.adorsys.datasafe.types.api.resource.Uri;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechDatasafe;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class DatasafeConfig {

    private final FintechStorage fintechStorage;

    @Bean
    public FintechDatasafe fintechDatasafeServices() {
        DFSConfig config = new FintechStorage.FintechDFSConfig("trum-pam-pam");
        OverridesRegistry overridesRegistry = new BaseOverridesRegistry();
        ProfileRetrievalServiceImplRuntimeDelegatable.overrideWith(overridesRegistry, FintechStorage.FintechUserRetrieval::new);
        PathEncryptionImplRuntimeDelegatable.overrideWith(overridesRegistry, NoOpPathEncryptionImplOverridden::new);
        return new FintechDatasafe(
                DaggerDefaultDatasafeServices.builder()
                        .config(config)
                        .storage(fintechStorage)
                        .overridesRegistry(overridesRegistry)
                        .build(),
                config
        );
    }

    // Path encryption that does not encrypt paths
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
