package de.adorsys.opba.protocol.hbci.service.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Transient data entry that should not be persisted.
 */
@Getter
@RequiredArgsConstructor
public class TransientDataEntry {

    private final String psuPin;
    private final String tanValue;
}
