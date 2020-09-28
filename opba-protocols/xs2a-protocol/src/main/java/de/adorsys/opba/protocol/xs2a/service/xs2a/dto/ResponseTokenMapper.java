package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import com.google.common.base.Strings;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
public interface ResponseTokenMapper {

    default String map(TokenResponse response) {
        if (null == response) {
            return null;
        }

        if (!Strings.isNullOrEmpty(response.getTokenType())) {
            return response.getTokenType() + " " + response.getAccessToken();
        }

        return response.getAccessToken();
    }
}
