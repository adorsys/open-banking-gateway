package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import com.google.common.collect.Sets;
import de.adorsys.opba.protocol.xs2a.config.aspspmessages.AspspMessages;
import de.adorsys.xs2a.adapter.service.exception.ErrorResponseException;
import de.adorsys.xs2a.adapter.service.model.TppMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateConsentErrorSink {

    private final AspspMessages messageConfig;

    public void swallowConsentCreationErrorForLooping(Runnable tryAuthorize, Consumer<ErrorResponseException> onFail) {
        try {
            tryAuthorize.run();
        } catch (ErrorResponseException ex) {
            rethrowIfNotCorrectErrorCode(ex);
            onFail.accept(ex);
        }
    }

    private void rethrowIfNotCorrectErrorCode(ErrorResponseException ex) {
        if (!ex.getErrorResponse().isPresent() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        Set<String> tppMessageCodes = ex.getErrorResponse().get().getTppMessages().stream()
                .map(TppMessage::getCode)
                .collect(Collectors.toSet());

        if (Sets.intersection(messageConfig.getInvalidConsent(), tppMessageCodes).isEmpty())  {
            throw ex;
        }
    }
}
