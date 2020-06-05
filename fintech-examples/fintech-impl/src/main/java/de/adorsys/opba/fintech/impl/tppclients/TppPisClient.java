package de.adorsys.opba.fintech.impl.tppclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tpp.pis.api.resource.generated.TppBankingApiPaymentInitiationServicePisApi;
import org.springframework.cloud.openfeign.FeignClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@FeignClient(url = "${tpp.url}", name = "tppPisClient")
public interface TppPisClient extends TppBankingApiPaymentInitiationServicePisApi {
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
