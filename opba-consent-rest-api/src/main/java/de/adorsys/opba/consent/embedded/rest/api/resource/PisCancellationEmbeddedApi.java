package de.adorsys.opba.consent.embedded.rest.api.resource;

import io.swagger.annotations.Api;

@Api(value = "/embedded-pis-cancellation", tags = "PSU PIS Cancelation", description = "Provides access to embedded (tpp driven) PIS cancellation functionality")
public interface PisCancellationEmbeddedApi extends PISEmbeddedApi {
}
