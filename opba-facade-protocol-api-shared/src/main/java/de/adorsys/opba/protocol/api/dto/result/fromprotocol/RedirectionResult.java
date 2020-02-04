package de.adorsys.opba.protocol.api.dto.result.fromprotocol;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor
public abstract class RedirectionResult<T> implements Result<T> {

    private URI redirectionTo;
}
