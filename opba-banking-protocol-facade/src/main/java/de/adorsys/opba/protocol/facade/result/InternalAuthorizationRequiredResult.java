package de.adorsys.opba.protocol.facade.result;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import lombok.Getter;

import java.net.URI;

@Getter
public class InternalAuthorizationRequiredResult<T, C> extends RedirectionResult<T, C> {

    private final SecretKeyWithIv authorizationKey;

    public InternalAuthorizationRequiredResult(SecretKeyWithIv authorizationKey) {
        super(URI.create(""), null);
        this.authorizationKey = authorizationKey;
    }
}
