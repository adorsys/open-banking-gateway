package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthStateBody implements ResultBody {

    private String action;
    private Set<ValidationError> violations;
    private Set<ScaMethod> scaMethods;

    private String redirectTo;
    private String redirectToUiScreen;

    // FIXME add type-type mapping
    private Object auth;
}
