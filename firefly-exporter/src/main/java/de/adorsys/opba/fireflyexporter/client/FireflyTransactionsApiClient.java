package de.adorsys.opba.fireflyexporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.firefly.api.resource.generated.TransactionsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${firefly.url}", name = "fireflyTransactionsApiClient", configuration = FireFlyClientConfig.class)
public interface FireflyTransactionsApiClient extends TransactionsApi {
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
