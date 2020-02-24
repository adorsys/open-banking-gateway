package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

@FunctionalInterface
public interface DtoUpdatingMapper<F, T> {
    T map(F from, T toUpdate);
}
