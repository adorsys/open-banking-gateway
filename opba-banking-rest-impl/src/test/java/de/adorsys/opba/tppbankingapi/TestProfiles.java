package de.adorsys.opba.tppbankingapi;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class TestProfiles {

    public static final String ONE_TIME_POSTGRES_ON_DISK = "test-one-time-postgres-disk-volume";
    public static final String ONE_TIME_POSTGRES_RAMFS = "test-one-time-postgres-ramfs";
    public static final String MIGRATION = "migration";
}
