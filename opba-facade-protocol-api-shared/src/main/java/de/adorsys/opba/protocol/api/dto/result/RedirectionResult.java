package de.adorsys.opba.protocol.api.dto.result;

import lombok.Data;

import java.net.URI;

@Data
public class RedirectionResult<T> implements Result<T> {

    private URI redirectionTo;
}
