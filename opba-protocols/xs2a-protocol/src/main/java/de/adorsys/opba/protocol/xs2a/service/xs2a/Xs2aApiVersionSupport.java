package de.adorsys.opba.protocol.xs2a.service.xs2a;

import com.vdurmont.semver4j.Semver;

import java.util.Set;


public interface Xs2aApiVersionSupport {

    Set<Semver.VersionDiff> VERSION_DIFFS = Set.of(Semver.VersionDiff.MAJOR, Semver.VersionDiff.MINOR);

    boolean isXs2aApiVersionSupported(String apiVersion);

}
