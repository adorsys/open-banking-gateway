package de.adorsys.opba.protocol.hbci;

import org.mapstruct.Mapper;

import java.util.UUID;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

/**
 * Mapper for mapstruct to convert from UUID to String.
 */
@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
public interface HbciUuidMapper {

    default String map(UUID from) {
        if (null == from) {
            return null;
        }

        return from.toString();
    }
}
