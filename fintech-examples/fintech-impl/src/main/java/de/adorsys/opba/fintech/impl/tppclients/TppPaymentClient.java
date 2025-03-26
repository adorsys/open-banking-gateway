package de.adorsys.opba.fintech.impl.tppclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.token.api.resource.generated.PaymentConfirmationApi;
import org.springframework.cloud.openfeign.FeignClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${tpp.url}", name = "tppPaymentClient")
public interface TppPaymentClient extends PaymentConfirmationApi {
    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }
}
