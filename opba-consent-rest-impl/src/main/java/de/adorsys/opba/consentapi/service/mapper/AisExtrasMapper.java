package de.adorsys.opba.consentapi.service.mapper;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraAuthRequestParam;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;

import java.util.Map;

import static de.adorsys.opba.restapi.shared.GlobalConst.CONSENT_MAPPERS_PACKAGE;
import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = CONSENT_MAPPERS_PACKAGE)
public interface AisExtrasMapper {

    @MapMapping(keyTargetType = ExtraAuthRequestParam.class)
    Map<ExtraAuthRequestParam, Object> map(Map<String, String> request);
}
