package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor
public abstract class RedirectionResult<T, C> implements Result<T> {

    private final URI redirectionTo;
    private final C cause;
}
