package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.Sets;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.config.aspspmessages.AspspMessages;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.exception.ErrorResponseException;
import de.adorsys.xs2a.adapter.api.exception.OAuthException;
import de.adorsys.xs2a.adapter.api.exception.PsuPasswordEncodingException;
import de.adorsys.xs2a.adapter.api.model.MessageCode;
import de.adorsys.xs2a.adapter.api.model.TppMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ServiceConfigurationError;
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
            tryHandleWrongIbanOrCredentialsExceptionOrOauth2(execution, ex);
        } catch (OAuthException ex) {
            tryHandleOauth2Exception(execution);
        } catch (ServiceConfigurationError ex) {
            // FIXME https://github.com/adorsys/xs2a-adapter/issues/577
            // FIXME https://github.com/adorsys/open-banking-gateway/issues/1199
            if (null != ex.getCause() && ex.getCause().getMessage().contains("Exception during Deutsche bank adapter PSU password encryption")) {
                log.error("Failed to initialize Deutsche bank encryption service, but ignoring it");
            } else {
                throw ex;
            }
        }
    }

    private void tryHandleWrongIbanOrCredentialsExceptionOrOauth2(DelegateExecution execution, ErrorResponseException ex) {
        if (!ex.getErrorResponse().isPresent() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        if (isWrongIban(ex)) {
            onWrongIban(execution);
            return;
        }

        // TODO: https://github.com/adorsys/open-banking-gateway/issues/976
        if (HttpStatus.UNAUTHORIZED.value() == ex.getStatusCode() && isPossiblyOauth2Error(ex)) {
            tryHandleOauth2Exception(execution);
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

    private boolean isPossiblyOauth2Error(ErrorResponseException ex) {
        Set<MessageCode> tppMessageCodes = ex.getErrorResponse().get().getTppMessages().stream()
                .map(TppMessage::getCode)
                .collect(Collectors.toSet());

        return !Sets.intersection(messageConfig.getMissingOauth2Token(), tppMessageCodes).isEmpty();
    }

    private boolean isWrongIban(ErrorResponseException ex) {
        Set<MessageCode> tppMessageCodes = ex.getErrorResponse().get().getTppMessages().stream()
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
