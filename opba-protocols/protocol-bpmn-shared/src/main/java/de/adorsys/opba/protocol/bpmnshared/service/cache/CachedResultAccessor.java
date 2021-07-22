package de.adorsys.opba.protocol.bpmnshared.service.cache;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ProtocolResultCache;
import de.adorsys.opba.protocol.bpmnshared.service.SafeCacheSerDeUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CachedResultAccessor<CONSENT, ACCOUNTS, TRANSACTIONS, CACHE extends ProtocolResultCache<CONSENT, ACCOUNTS, TRANSACTIONS>, CONTEXT extends BaseContext> {

    private final SafeCacheSerDeUtil safeCacheSerDe;

    @SneakyThrows
    @Transactional
    public Optional<CACHE> resultFromCache(CONTEXT context) {
        List<CACHE> consents = context.consentAccess().findByCurrentServiceSessionOrderByModifiedDesc()
                .stream()
                .map(target -> (CACHE) safeCacheSerDe.safeDeserialize(target.getConsentCache()))
                .collect(Collectors.toList());

        if (consents.isEmpty()) {
            return Optional.empty();
        }

        CACHE result = null;
        for (var consent : consents) {
            if (null == result) {
                result = consent;
            }
            if (null == consent) {
                continue;
            }

            mergeAccounts(result, consent);
            mergeTransactions(result, consent);
        }

        return Optional.ofNullable(result);
    }

    @SneakyThrows
    @Transactional
    public void resultToCache(CONTEXT context, CACHE result) {
        ProtocolFacingConsent newConsent = context.getRequestScoped().consentAccess().createDoNotPersist();
        newConsent.setConsentId(context.getSagaId());
        newConsent.setConsentCache(safeCacheSerDe.safeSerialize(result));
        context.getRequestScoped().consentAccess().save(newConsent);
    }

    private boolean consentIsNewer(CACHE result, CACHE consent) {
        if (null == result.getCachedAt()) {
            return true;
        }

        if (null == consent.getCachedAt()) {
            return false;
        }

        return consent.getCachedAt().isAfter(result.getCachedAt());
    }

    private void mergeAccounts(CACHE result, CACHE consent) {
        if (null == result.getAccounts()) {
            result.setAccounts(consent.getAccounts());
            return;
        }

        if (consentIsNewer(result, consent) && null != consent.getAccounts()) {
            result.setAccounts(consent.getAccounts());
        }
    }

    private void mergeTransactions(CACHE result, CACHE consent) {
        if (null == consent.getTransactionsById()) {
            return;
        }

        if (null == result.getTransactionsById()) {
            result.setTransactionsById(consent.getTransactionsById());
            return;
        }

        var computedTransactions = new HashMap<>(result.getTransactionsById());
        for (var newEntry : consent.getTransactionsById().entrySet()) {
            computedTransactions.compute(newEntry.getKey(), (id, existing) -> {
                if (null == existing) {
                    return newEntry.getValue();
                }

                if (consentIsNewer(result, consent) && null != newEntry.getValue()) {
                    return newEntry.getValue();
                }

                return existing;
            });
        }
        result.setTransactionsById(computedTransactions);
    }
}
