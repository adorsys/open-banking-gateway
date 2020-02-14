package de.adorsys.opba.protocol.facade;

import lombok.Value;

@Value
public class ContextWithKey {
    String encryptedContext;
    KeyDTO keyDTO;
}
