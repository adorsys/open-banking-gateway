package de.adorsys.opba.fintech.impl.tppclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.banksearch.api.resource.generated.TppBankSearchApi;
import org.springframework.cloud.openfeign.FeignClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${tpp.url}", name = "tppSearch")
public interface TppBankSearchClient extends TppBankSearchApi {
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
