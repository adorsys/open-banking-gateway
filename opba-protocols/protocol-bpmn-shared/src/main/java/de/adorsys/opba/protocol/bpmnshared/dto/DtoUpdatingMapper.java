package de.adorsys.opba.protocol.bpmnshared.dto;

/**
 * Mapper interface that updates one DTO using another.
 *
 * @param <F> Source DTO
 * @param <T> Target DTO that will be updated with <b>Source DTO</b>
 */
@FunctionalInterface
public interface DtoUpdatingMapper<F, T> {

    T map(F from, T toUpdate);
}
