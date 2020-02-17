package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacadeRedirectResult<T> extends FacadeResultRedirectable<T> {

    public static final RedirectFromProtocol FROM_PROTOCOL = Mappers.getMapper(RedirectFromProtocol.class);

    @Mapper
    public interface RedirectFromProtocol {
        FacadeRedirectResult map(RedirectionResult result);
    }
}
