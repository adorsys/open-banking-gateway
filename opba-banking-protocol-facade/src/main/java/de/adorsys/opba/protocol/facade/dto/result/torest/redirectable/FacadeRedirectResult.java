package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Successful result that indicates PSU/Fintech user should be redirected elsewhere.
 * @param <T> Result body type
 * @param <C> Authorization state
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FacadeRedirectResult<T, C extends AuthStateBody> extends FacadeResultRedirectable<T, C> {

    public static final RedirectFromProtocol FROM_PROTOCOL = Mappers.getMapper(RedirectFromProtocol.class);

    @Mapper
    public interface RedirectFromProtocol {

        @Mapping(target = "cause", ignore = true)
        FacadeRedirectResult map(RedirectionResult result);
    }
}
