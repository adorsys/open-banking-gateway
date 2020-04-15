package de.adorsys.opba.protocol.api.dto.context;

import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class ServiceContext<T> {

    private final Long serviceBankProtocolId;
    private final Long authorizationBankProtocolId;

    private final String bankId;

    @NonNull
    private final UUID serviceSessionId;

    private final UUID authSessionId;

    /**
     * Will be used as redirect code when coming back from ASPSP.
     */
    private final UUID futureAspspRedirectCode;

    /**
     * Will be used as redirect code only when authorization session is opened or continued.
     */
    private final UUID futureRedirectCode;

    /**
     * Will be used as new authorization session id only when authorization session is opened.
     */
    private final UUID futureAuthSessionId;

    @NonNull
    private final T request;

    private final String authContext;

    private final RequestScoped requestScoped;

    public String loggableBankId() {
        return String.format(
                "[protocol id: %s / bank uuid: %s]",
                getServiceBankProtocolId(),
                getBankId()
        );
    }
}
