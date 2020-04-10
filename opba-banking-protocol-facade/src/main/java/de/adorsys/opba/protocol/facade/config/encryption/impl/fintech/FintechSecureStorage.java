package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class FintechSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;

    public void registerFintech(Fintech fintech, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        fintech.getUserIdAuth(password),
                        config.defaultPrivateTemplate(fintech.getUserIdAuth(password)).buildPrivateProfile()
                );
    }
}
