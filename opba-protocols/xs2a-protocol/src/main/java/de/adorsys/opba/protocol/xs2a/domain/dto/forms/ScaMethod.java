package de.adorsys.opba.protocol.xs2a.domain.dto.forms;

import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;

/**
 * Class that represents available SCA methods and their names. I.e. SMS based second factor authorization,
 * email based second factor authorization.
 */
@Data
public class ScaMethod {

    public static final ScaMethod.FromAuthObject FROM_AUTH = Mappers.getMapper(ScaMethod.FromAuthObject.class);

    /**
     * The ID of SCA method.
     */
    private String key;

    /**
     * User friendly name (caption) for SCA method.
     */
    private String value;

    /**
     * SCA method type (email, sms etc.)
     */
    private String type;

    @Mapper(componentModel = SPRING_KEYWORD)
    public interface FromAuthObject {
        @Mapping(source = "authenticationMethodId", target = "key")
        @Mapping(target = "value", expression = "java(\"\" + auth.getAuthenticationType() + ':' + auth.getName())")
        @Mapping(source = "authenticationType", target = "type")
        ScaMethod map(AuthenticationObject auth);
    }

    /**
     * @return JSON representation of SCA method available.
     */
    @Override
    public String toString() {
        return "{"
                       + "\"key\":\"" + key + "\""
                       + ", \"value\":\"" + value + "\""
                       + ", \"type\":\"" + type + "\""
                       + "}";
    }
}
