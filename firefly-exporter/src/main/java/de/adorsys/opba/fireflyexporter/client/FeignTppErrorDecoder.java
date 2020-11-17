package de.adorsys.opba.fireflyexporter.client;

import de.adorsys.opba.fireflyexporter.controller.exceptions.ConsentException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Slf4j
public class FeignTppErrorDecoder implements ErrorDecoder {
    public static final String X_ERROR_CODE = "X-ERROR-CODE";

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.GONE.value() || response.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            Optional<String> first = response.headers().get(X_ERROR_CODE).stream().findFirst();
            if (first.isPresent()) {
                return new ConsentException();
            }

            log.error("Error during error handling. Expected header wih {}", X_ERROR_CODE);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}

