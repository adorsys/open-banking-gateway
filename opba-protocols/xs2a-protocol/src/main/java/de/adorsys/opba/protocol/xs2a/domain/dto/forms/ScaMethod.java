package de.adorsys.opba.protocol.xs2a.domain.dto.forms;

import de.adorsys.xs2a.adapter.service.model.AuthenticationObject;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Data
public class ScaMethod {

    public static final ScaMethod.FromAuthObject FROM_AUTH = Mappers.getMapper(ScaMethod.FromAuthObject.class);

    private String key;
    private String value;

    @Mapper
    public interface FromAuthObject {
        @Mapping(source = "authenticationMethodId", target = "key")
        @Mapping(target = "value",  expression = "java(auth.getAuthenticationType() + ':' + auth.getName())")
        ScaMethod map(AuthenticationObject auth);
    }

    @Override
    public String toString() {
        return "{"
            + "\"key\":\"" + key + "\""
            + ", \"value\":\"" + value + "\""
            + "}";
    }
}
