package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import org.springframework.stereotype.Service;

@Service
public class TppRedirectPreferredResolver {

    public Boolean isRedirectApproachPreferred(CurrentBankProfile config) {
        return config.isTryToUsePreferredApproach()
                ? config.getPreferredApproach() == Approach.REDIRECT
                : null;
    }
}
