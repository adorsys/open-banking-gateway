package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.protocol.api.dto.result.ErrorResult;

@FunctionalInterface
public interface ErrorResultMapper<F extends ErrorResult, T> {
    T map(F from);
}
