package de.adorsys.opba.protocol.xs2a.entrypoint.helpers;

import org.mapstruct.Mapper;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Mapper for mapstruct to convert from UUID to String.
 */
@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
public interface UuidMapper {

    default String map(UUID from) {
        if (null == from) {
            return null;
        }

        return from.toString();
    }
}
