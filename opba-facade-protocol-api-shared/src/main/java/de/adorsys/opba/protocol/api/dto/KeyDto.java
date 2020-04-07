package de.adorsys.opba.protocol.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeyDto {

    protected final String id;
    protected final byte[] key;
}
