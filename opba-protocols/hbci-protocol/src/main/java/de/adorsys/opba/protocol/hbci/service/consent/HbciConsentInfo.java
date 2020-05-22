package de.adorsys.opba.protocol.hbci.service.consent;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import org.springframework.stereotype.Service;

/**
 * Generic information service about the consent based on current context.
 */
@Service("hbciConsentInfo")
public class HbciConsentInfo {

    /**
     * Is the PSU ID present in the context.
     */
    public boolean isPsuIdPresent(HbciContext ctx) {
        return !Strings.isNullOrEmpty(ctx.getPsuId());
    }

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
        return ctx.isTanChallengeRequired();
    }

    /**
     * Any kind of consent exists?
     */
    public boolean isCachedAccountListMissing(AccountListHbciContext ctx) {
        return null == ctx.getResponse();
    }
}
