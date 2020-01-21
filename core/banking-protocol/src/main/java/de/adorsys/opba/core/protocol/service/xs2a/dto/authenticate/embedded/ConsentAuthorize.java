package de.adorsys.opba.core.protocol.service.xs2a.dto.authenticate.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
public class ConsentAuthorize {

    @NonNull
    @Valid
    private ConsentAuthorizeHeaders headers;

    @NonNull
    @Valid
    private ConsentAuthorizeBody body;
}
