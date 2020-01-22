package de.adorsys.opba.core.protocol.service.xs2a.dto;

public interface DtoMapper<F, T> {
    T map(F from);
}
