package de.adorsys.opba.fintech.impl.database.entities;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestHashing {
    private static final String originalValue = "PETER";
    private static final String hashedAndHexed = "723ce87fb5560c8d2e0cffad1b198cb0526fcef0a27b509c8e23be14d3e0b506";
    @Test
    public void testHash() {
        Assertions.assertEquals(hashedAndHexed, SessionEntity.hashAndHexconvert(originalValue));
    }
}
