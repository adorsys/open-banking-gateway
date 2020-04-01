package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class PsuDatasafe implements DefaultDatasafeServices {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;
}
