package de.adorsys.opba.protocol.facade.dto.result.torest;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.RedirectionResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.net.URI;

@Data
public class FacadeRedirectResult<T> implements FacadeResult<T> {

    public static final RedirectFromProtocol FROM_PROTOCOL = Mappers.getMapper(RedirectFromProtocol.class);

    private String authorizationSessionId;
    private URI redirectionTo;

    @Mapper
    public interface RedirectFromProtocol {
        FacadeRedirectResult map(RedirectionResult result);
    }
}
