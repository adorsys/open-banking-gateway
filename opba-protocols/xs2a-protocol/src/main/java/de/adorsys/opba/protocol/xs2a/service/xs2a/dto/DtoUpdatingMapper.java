package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

/**
 * Mapper interface that updates one DTO using another.
 * @param <F> Source DTO
 * @param <T> Target DTO that will be updated with <b>Source DTO</b>
 */
@FunctionalInterface
public interface DtoUpdatingMapper<F, T> {

    T map(F from, T toUpdate);
}
