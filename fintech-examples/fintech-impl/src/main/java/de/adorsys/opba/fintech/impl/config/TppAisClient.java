package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${tpp.url}", name = "tppAisClient")
public interface TppAisClient extends TppBankingApiAccountInformationServiceAisApi {
}
