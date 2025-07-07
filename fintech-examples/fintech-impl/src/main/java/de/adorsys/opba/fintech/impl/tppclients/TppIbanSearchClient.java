package de.adorsys.opba.fintech.impl.tppclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.bankinfo.api.resource.generated.TppBankInfoApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Optional;

@FeignClient(url = "${tpp.url}", name = "tppIbanSearch")
public interface TppIbanSearchClient extends TppBankInfoApi {

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
