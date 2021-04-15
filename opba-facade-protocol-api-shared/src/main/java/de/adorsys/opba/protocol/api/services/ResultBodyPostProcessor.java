package de.adorsys.opba.protocol.api.services;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;

public interface ResultBodyPostProcessor {

    Object apply(Object requestMappedResult);
    boolean shouldApply(FacadeServiceableRequest request, Object requestMappedResult);
}
