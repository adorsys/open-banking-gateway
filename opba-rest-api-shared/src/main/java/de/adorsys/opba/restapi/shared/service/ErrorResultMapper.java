package de.adorsys.opba.restapi.shared.service;

import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeErrorResult;

@FunctionalInterface
public interface ErrorResultMapper<F extends FacadeErrorResult, T> {
    T map(F from);
}
