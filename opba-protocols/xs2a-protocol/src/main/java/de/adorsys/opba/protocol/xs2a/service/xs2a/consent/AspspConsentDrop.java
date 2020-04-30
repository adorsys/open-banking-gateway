package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Performs ASPSP API call to drop the consent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AspspConsentDrop {

    private final RetryOperations retryOperations;
    private final AccountInformationService ais;
    private final DtoMapper<Xs2aAisContext, ConsentInitiateHeaders> mapper;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void dropConsent(Xs2aAisContext context) {
        retryOperations.execute(
                callback -> ais.deleteConsent(context.getConsentId(), mapper.map(context).toHeaders()),
                recover -> {
                    // Due to some transient unrecoverable error removal has failed, but it makes no sense to
                    // drop users request, consent will not be usable by us and still can be revoked manually
                    // on ASPSP side
                    log.error("Failed to delete consent of process {}", context.getSagaId(), recover.getLastThrowable());
                    return null;
                }
        );
    }
}
