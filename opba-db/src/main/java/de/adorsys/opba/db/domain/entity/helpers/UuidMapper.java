package de.adorsys.opba.db.domain.entity.helpers;

import org.mapstruct.Mapper;

import java.util.UUID;

/**
 * Mapper for mapstruct to convert from UUID to String.
 */
@Mapper
public interface UuidMapper {

    default String map(UUID from) {
        if (null == from) {
            return null;
        }

        return from.toString();
    }
}
