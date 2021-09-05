package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.Const;
import org.mapstruct.Mapper;

import java.util.UUID;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
public interface UuidMapper {
    default String map(UUID from) {
        if (null == from) {
            return null;
        }

        return from.toString();
    }
}
