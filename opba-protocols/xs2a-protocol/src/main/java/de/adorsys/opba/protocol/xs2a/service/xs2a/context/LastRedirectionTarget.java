package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.xs2a.service.storage.NeedsEncryptionService;
import de.adorsys.opba.protocol.xs2a.service.storage.PersistenceShouldUseEncryption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// TODO - decide do we need to encrypt these
public class LastRedirectionTarget implements NeedsEncryptionService, PersistenceShouldUseEncryption {

    private String redirectTo;
    private String redirectToUiScreen;

    /**
     * Encryption service provider for sensitive data.
     */
    @JsonIgnore
    private EncryptionService encryption;
}
