package de.adorsys.opba.fintech.impl.tppclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.token.api.resource.generated.ConsentConfirmationApi;
import org.springframework.cloud.openfeign.FeignClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${tpp.url}", name = "tppConsentClient")
public interface TppConsenClient extends ConsentConfirmationApi {
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
