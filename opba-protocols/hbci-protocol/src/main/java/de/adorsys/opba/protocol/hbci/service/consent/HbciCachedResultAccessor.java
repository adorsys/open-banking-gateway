package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.hbci.SafeCacheSerDeUtil;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HbciCachedResultAccessor {

    private final SafeCacheSerDeUtil safeCacheSerDe;

    @SneakyThrows
    @Transactional
    public Optional<HbciResultCache> resultFromCache(HbciContext context) {
        List<HbciResultCache> consents = context.consentAccess().findByCurrentServiceSessionOrderByModifiedDesc()
                .stream()
                .map(target -> (HbciResultCache) safeCacheSerDe.safeDeserialize(target.getConsentContext()))
                .collect(Collectors.toList());

        if (consents.isEmpty()) {
            return Optional.empty();
        }

        HbciResultCache result = new HbciResultCache();
        for (HbciResultCache consent : consents) {
            if (checkCacheIsNewer(result, consent)) {
                result.setAccounts(consent.getAccounts());
            }

            if (null != consent.getTransactionsByIban()) {
                if (null == result.getTransactionsByIban()) {
                    result.setTransactionsByIban(new HashMap<>());
                }

                mergeTransactions(result, consent);
            }
        }

        result.setConsent(consents.get(0).getConsent());
        return Optional.of(result);
    }

    @SuppressWarnings("PMD.UselessParentheses") // Parentheses are used for readability
    private boolean checkCacheIsNewer(HbciResultCache result, HbciResultCache consent) {
        return null == result.getAccounts()
                || (null != consent.getAccounts() && consent.getAccounts().getCachedAt().isAfter(result.getAccounts().getCachedAt()));
    }

    private void mergeTransactions(HbciResultCache result, HbciResultCache consent) {
        consent.getTransactionsByIban().forEach((iban, txn) -> result.getTransactionsByIban().compute(iban, (id, current) -> {
            if (null == current) {
                return txn;
            }
            if (txn.getCachedAt().isAfter(current.getCachedAt())) {
                return txn;
            }
            return current;
        }));
    }

    @SneakyThrows
    @Transactional
    public void resultToCache(HbciContext context, HbciResultCache result) {
        ProtocolFacingConsent newConsent = context.getRequestScoped().consentAccess().createDoNotPersist();
        newConsent.setConsentId(context.getSagaId());
        newConsent.setConsentContext(safeCacheSerDe.safeSerialize(result));
        context.getRequestScoped().consentAccess().save(newConsent);
    }
}
