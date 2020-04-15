package de.adorsys.opba.protocol.api.common;

import java.util.List;

public interface CurrentBankProfile {

    Long getId();
    String getUrl();
    String getAdapterId();
    String getIdpUrl();
    List<Approach> getScaApproaches();
    Approach getPreferredApproach();
}
