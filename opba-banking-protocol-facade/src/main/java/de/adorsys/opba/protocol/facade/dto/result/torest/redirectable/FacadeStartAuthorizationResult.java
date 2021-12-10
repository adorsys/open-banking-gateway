package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Facade result indicating Consent/Payment session authorization is necessary.
 * @param <T> Result body
 * @param <C> Authorization state
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FacadeStartAuthorizationResult<T, C extends AuthStateBody> extends FacadeResultRedirectable<T, C> {

    public static final RedirectFromProtocol FROM_PROTOCOL = Mappers.getMapper(RedirectFromProtocol.class);

    @Mapper
    public interface RedirectFromProtocol {

        @Mapping(target = "cause", ignore = true)
        FacadeStartAuthorizationResult map(RedirectionResult result);
    }
}
