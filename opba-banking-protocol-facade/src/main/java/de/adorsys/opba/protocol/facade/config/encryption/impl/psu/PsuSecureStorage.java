package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class PsuSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;

    public void registerPsu(UserIDAuth auth) {
        this.userProfile()
                .createDocumentKeystore(
                        auth,
                        config.defaultPrivateTemplate(auth).buildPrivateProfile()
                );
    }
}
