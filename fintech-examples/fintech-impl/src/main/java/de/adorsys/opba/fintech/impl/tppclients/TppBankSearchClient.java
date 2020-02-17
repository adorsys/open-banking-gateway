package de.adorsys.opba.fintech.impl.tppclients;

import de.adorsys.opba.tpp.banksearch.api.resource.generated.TppBankSearchApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${tpp.url}", name = "tppSearch")
public interface TppBankSearchClient extends TppBankSearchApi {
}
