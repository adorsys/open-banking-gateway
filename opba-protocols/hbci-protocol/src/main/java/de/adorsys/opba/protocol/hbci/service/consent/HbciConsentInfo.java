package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
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
        return ctx.isTanChallengeRequired();
    }

    /**
     * Any kind of list account consent exists?
     */
    public boolean isCachedAccountListMissing(AccountListHbciContext ctx) {
        return null == ctx.getResponse();
    }

    /**
     * Any kind of list transaction consent exists?
     */
    public boolean isCachedTransactionListMissing(TransactionListHbciContext ctx) {
        return null == ctx.getResponse();
    }

    /**
     * Any kind of consent exists?
     */
    public boolean noConsentPresent(HbciContext ctx) {
        return ctx.isConsentIncompatible()
                || !ctx.getRequestScoped().consentAccess().findByCurrentServiceSession().isPresent();
    }
}
