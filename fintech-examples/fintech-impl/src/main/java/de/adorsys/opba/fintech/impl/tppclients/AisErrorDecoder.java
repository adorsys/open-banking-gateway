package de.adorsys.opba.fintech.impl.tppclients;

import de.adorsys.opba.fintech.impl.exceptions.ConsentException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class AisErrorDecoder implements ErrorDecoder {
    public static final String X_ERROR_CODE = "X-ERROR-CODE";
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 410 || response.status() == 429) {
            Optional<String> first = response.headers().get(X_ERROR_CODE).stream().findFirst();
            if (first.isPresent()) {
                return new ConsentException(response.status(), Integer.valueOf(first.get()));
            }
            log.error("Error during error handling. Excpeted headerfield wih {}", X_ERROR_CODE);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
