package de.adorsys.opba.protocol.xs2a.service.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Transient data entry that should not be persisted.
 */
@Getter
@RequiredArgsConstructor
public class TransientDataEntry {

    private final String psuPassword;
    private final String scaChallengeResult;
}
