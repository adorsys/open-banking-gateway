package de.adorsys.opba.fireflyexporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${open-banking.url}", name = "tppAisClient", configuration = TppClientConfig.class)
public interface TppAisClient extends TppBankingApiAccountInformationServiceAisApi {
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
