package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

/**
 * Mapper interface from one DTO to another.
 * @param <F> Source DTO
 * @param <T> Target DTO
 */
@FunctionalInterface
public interface DtoMapper<F, T> {

    T map(F from);
}
