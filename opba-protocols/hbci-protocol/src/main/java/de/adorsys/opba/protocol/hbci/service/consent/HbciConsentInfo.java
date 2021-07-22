package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.hbci.model.HbciConsent;
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
     * Check that password present in consent (needed for getting payment status without new interactive authorization).
     */
    public boolean isPasswordPresentInConsent(HbciContext ctx) {
        HbciConsent hbciDialogConsent = ctx.getHbciDialogConsent();
        if (hbciDialogConsent == null) {
            return false;
        }

        Credentials credentials = hbciDialogConsent.getCredentials();
        if (credentials == null) {
            return false;
        }

        return null != credentials.getPin();
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
                hbciResultCache -> null == hbciResultCache.getTransactionsById()
                        || null == hbciResultCache.getTransactionsById().get(ctx.getAccountIban())
        ).orElse(true);
    }

    /**
     * Was the PSU password that was sent to ASPSP wrong.
     */
    public boolean isWrongPassword(HbciContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    /**
     * Was the SCA challenge result that was sent to ASPSP wrong.
     */
    public boolean isWrongScaChallenge(HbciContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }
}
