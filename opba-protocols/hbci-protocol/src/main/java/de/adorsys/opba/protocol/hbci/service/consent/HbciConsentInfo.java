package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.hbci.context.HbciContext;
import org.springframework.stereotype.Service;

/**
 * Generic information service about the consent based on current context.
 */
@Service("hbciConsentInfo")
public class HbciConsentInfo {

    /**
     * Is the PSU password present in the context.
     */
    public boolean isPasswordPresent(HbciContext ctx) {
        return null != ctx.getPsuPin();
    }

    /**
     * Is the TAN challenge required.
     */
    public boolean isTanChallengeRequired(HbciContext ctx) {
        return null != ctx.getPsuPin();
    }
}
