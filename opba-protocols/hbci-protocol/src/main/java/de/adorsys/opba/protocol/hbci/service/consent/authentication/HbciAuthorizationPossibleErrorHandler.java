package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.exception.MultibankingError;
import de.adorsys.multibanking.domain.exception.MultibankingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Special service to parse ASPSP authorization responses on certain error conditions. For example used to catch
 * exception on wrong PIN/password input and if it is retryable to swallow the exception and call fallback
 * routine.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HbciAuthorizationPossibleErrorHandler {

    /**
     * Swallows retryable (like wrong password) authorization exceptions.
     * @param tryAuthorize Authorization function to call
     * @param onFail Fallback function to call if retryable exception occurred.
     */
    public void handlePossibleAuthorizationError(Runnable tryAuthorize, Consumer<MultibankingException> onFail) {
        try {
            tryAuthorize.run();
        } catch (MultibankingException ex) {
            rethrowIfNotAuthorizationErrorCode(ex);
            onFail.accept(ex);
        }
    }

    private void rethrowIfNotAuthorizationErrorCode(MultibankingException ex) {
        if (null == ex.getMultibankingError()) {
            throw ex;
        }
        if (ex.getMultibankingError().equals(MultibankingError.INVALID_PIN)) {
            log.warn("wrong pin was entered");
            return;
        }
        if (ex.getMultibankingError().equals(MultibankingError.INVALID_TAN)) {
            log.warn("wrong tan was entered");
            return;
        }
        if (ex.getMultibankingError().equals(MultibankingError.HBCI_ERROR)) {
            log.warn("hbci error");
            return;
        }
        throw ex;
    }
}
