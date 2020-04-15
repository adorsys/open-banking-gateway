package de.adorsys.opba.fintech.impl.tppclients;

import de.adorsys.opba.tpp.token.api.resource.generated.ConsentConfirmationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${tpp.url}", name = "tppConsentClient")
public interface TppConsenClient extends ConsentConfirmationApi {
}
