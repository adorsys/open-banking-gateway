package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Redirect extends InternalProcessResult {

    @NonNull
    private URI redirectUri;

    @Builder
    public Redirect(String processId, String executionId, Object result, URI redirectUri) {
        super(processId, executionId, result);
        this.redirectUri = redirectUri;
    }
}
