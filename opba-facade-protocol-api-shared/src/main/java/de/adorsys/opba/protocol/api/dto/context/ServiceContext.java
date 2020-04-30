package de.adorsys.opba.protocol.api.dto.context;

import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Delegate;

@Getter
@Builder
@Value
public class ServiceContext<T>  {
    @Delegate
    Context<T> ctx;
    /**
     * Services that are provided by Facade to the protocol (i.e. Encryption)
     */
    RequestScoped requestScoped;
}
