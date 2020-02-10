package de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FromAspspRequest implements FacadeServiceableGetter {

    private FacadeServiceableRequest facadeServiceable;

    @NonNull
    private Boolean isOk;
}
