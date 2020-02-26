package de.adorsys.opba.restapi.shared.mapper;

public class NoOpMapper<T> implements FacadeResponseBodyToRestBodyMapper<T, T> {
    public T map(T facadeEntity) {
        return facadeEntity;
    }
}
