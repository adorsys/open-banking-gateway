package de.adorsys.opba.protocol.bpmnshared.dto;

/**
 * Mapper interface from one DTO to another.
 * @param <F> Source DTO
 * @param <T> Target DTO
 */
@FunctionalInterface
public interface DtoMapper<F, T> {

    T map(F from);
}
