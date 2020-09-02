package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.Sets;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.config.aspspmessages.AspspMessages;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.exception.ErrorResponseException;
import de.adorsys.xs2a.adapter.service.exception.OAuthException;
import de.adorsys.xs2a.adapter.service.model.TppMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Special service to parse ASPSP consent create/initiate responses on certain error conditions. For example used to catch
 * exception on wrong IBAN input and if it is retryable to swallow the exception and call fallback
 * routine.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateConsentOrPaymentPossibleErrorHandler {

    private final AspspMessages messageConfig;

    /**
     * Swallows retryable (like wrong IBAN) consent initiation exceptions.
     * @param tryCreate Consent/payment creation function to call
     */
    public void tryCreateAndHandleErrors(DelegateExecution execution, Runnable tryCreate) {
        try {
            tryCreate.run();
        } catch (ErrorResponseException ex) {
            tryHandleWrongIbanOrCredentialsException(execution, ex);
        } catch (OAuthException ex) {
            tryHandleOauth2Exception(execution);
        }
    }

    private void tryHandleWrongIbanOrCredentialsException(DelegateExecution execution, ErrorResponseException ex) {
        if (!ex.getErrorResponse().isPresent() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        if (isWrongIban(ex)) {
            onWrongIban(execution);
            return;
        }

        throw ex;
    }

    private void tryHandleOauth2Exception(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    checkAndHandleIrrecoverableOAuth2State(ctx);
                    ctx.setOauth2PreStepNeeded(true);
                }
        );
    }

    private boolean isWrongIban(ErrorResponseException ex) {
        Set<String> tppMessageCodes = ex.getErrorResponse().get().getTppMessages().stream()
                .map(TppMessage::getCode)
                .collect(Collectors.toSet());

        return !Sets.intersection(messageConfig.getInvalidConsent(), tppMessageCodes).isEmpty();
    }

    private void onWrongIban(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect IBAN", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }

    private void checkAndHandleIrrecoverableOAuth2State(Xs2aContext ctx) {
        if (null != ctx.getOauth2Token() && null != ctx.getOauth2Code()) {
            throw new RuntimeException("Unable to handle Oauth2 exception as code and token are already not null");
        }
    }
}
