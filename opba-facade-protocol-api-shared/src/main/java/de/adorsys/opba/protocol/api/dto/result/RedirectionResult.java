package de.adorsys.opba.protocol.api.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor
public class RedirectionResult<T> implements Result<T> {

    private URI redirectionTo;
}
