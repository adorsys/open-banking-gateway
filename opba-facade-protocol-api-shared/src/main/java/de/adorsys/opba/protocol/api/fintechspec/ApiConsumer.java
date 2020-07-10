package de.adorsys.opba.protocol.api.fintechspec;

import de.adorsys.opba.protocol.api.common.CurrentFintechProfile;

public interface ApiConsumer extends CurrentFintechProfile {
    String getName();

    String getPublicKey();
}
