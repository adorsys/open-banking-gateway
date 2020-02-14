package de.adorsys.opba.protocol.facade.dto;

import lombok.Value;

@Value
public class KeyDTO {
    final byte[] key;
    final byte[] salt;
}
