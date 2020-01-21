package de.adorsys.opba.core.protocol.service.xs2a.dto.consent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
public class ConsentInitiate {

    @NonNull
    @Valid
    private ConsentInitiateHeaders headers;

    @NonNull
    @Valid
    private ConsentInitiateBody body;
}
