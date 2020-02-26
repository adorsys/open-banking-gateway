package de.adorsys.opba.restapi.shared.mapper;

@FunctionalInterface
public interface FacadeResponseBodyToRestBodyMapper<R, F> {
    R map(F facadeEntity);
}
