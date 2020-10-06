package de.adorsys.opba.protocol.xs2a.service.feign;

import de.adorsys.xs2a.adapter.api.ConsentApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${xs2a.auth.url}", name = "xs2aAuthorisationClient")
public interface Xs2aAuthorisationClient extends ConsentApi {
}
