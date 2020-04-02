package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.Value;
import lombok.experimental.Delegate;

@Value
public class ServiceSessionWithEncryption {

    @Delegate
    private ServiceSession serviceSession;

    private EncryptionService encryption;
}
