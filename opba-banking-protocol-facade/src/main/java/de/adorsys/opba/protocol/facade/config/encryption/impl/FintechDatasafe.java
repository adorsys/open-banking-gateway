package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class FintechDatasafe {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;

    public void registerFintech(UserIDAuth auth) {
        this.userProfile()
                .createDocumentKeystore(
                        auth,
                        config.defaultPrivateTemplate(auth).buildPrivateProfile()
                );
    }
}
