package de.adorsys.opba.protocol.facade.dto;

import lombok.Value;

@Value
public class ContextWithKey {
    String encryptedContext;
    KeyDTO keyDTO;
}
