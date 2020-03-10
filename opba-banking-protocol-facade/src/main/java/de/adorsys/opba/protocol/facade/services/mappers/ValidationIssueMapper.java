package de.adorsys.opba.protocol.facade.services.mappers;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.Cause;
import de.adorsys.opba.protocol.facade.services.CauseMapper;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.facade.services.mappers.Const.FACADE_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.facade.services.mappers.Const.SPRING_KEYWORD;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = FACADE_MAPPERS_PACKAGE)
public interface ValidationIssueMapper extends CauseMapper<ValidationIssue, Cause> {
}
