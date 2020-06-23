package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Generic information service about the consent based on current context.
 */
@Service("hbciConsentInfo")
@RequiredArgsConstructor
public class HbciConsentInfo {

    private final HbciCachedResultAccessor cachedResultAccessor;

    /**
     * Any kind of consent exists?
     */
    public boolean isUnderFintechScope(HbciContext ctx) {
        return ctx.getRequestScoped().consentAccess().isFinTechScope();
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
    public boolean noAccountsConsentPresent(AccountListHbciContext ctx) {
        if (ctx.isConsentIncompatible()) {
            return true;
        }

        Optional<HbciResultCache> cached = cachedResultAccessor.resultFromCache(ctx);
        return cached.map(hbciResultCache -> null == hbciResultCache.getAccounts()).orElse(true);
    }

    /**
     * Any kind of consent exists?
     */
    public boolean noTransactionConsentPresent(TransactionListHbciContext ctx) {
        if (ctx.isConsentIncompatible()) {
            return true;
        }

        Optional<HbciResultCache> cached = cachedResultAccessor.resultFromCache(ctx);
        return cached.map(
                hbciResultCache -> null == hbciResultCache.getTransactionsByIban()
                        || null == hbciResultCache.getTransactionsByIban().get(ctx.getAccountIban())
        ).orElse(true);
    }
}
