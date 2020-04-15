package de.adorsys.opba.protocol.api.consent;

import java.util.List;

public interface ConsentAccess {

    List<Consent> getAvailableConsentsForCurrentPsu();
}
