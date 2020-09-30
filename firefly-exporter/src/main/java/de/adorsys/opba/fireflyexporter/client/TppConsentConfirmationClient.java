package de.adorsys.opba.fireflyexporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.token.api.resource.generated.ConsentConfirmationApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${open-banking.url}", name = "tppConsentConfirmationClient", configuration = TppClientConfig.class)
public interface TppConsentConfirmationClient extends ConsentConfirmationApi {
    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader(HttpHeaders.ACCEPT));
    }
}
