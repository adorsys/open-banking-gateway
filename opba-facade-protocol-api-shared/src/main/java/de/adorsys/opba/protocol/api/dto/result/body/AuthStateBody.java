package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * State of current authorization - for example PSU ID required from user.
 */
@Data
@AllArgsConstructor
public class AuthStateBody implements ResultBody {

    /**
     * Current protocol action - i.e. list accounts or list transactions.
     * See {@code de.adorsys.opba.consentapi.model.generated.ActionEnum}
     */
    private String action;

    /**
     * What inputs are required from user - i.e. PSU ID.
     */
    private Set<ValidationError> violations;

    /**
     * Which SCA methods are available for consent authorization (i.e. SMS, email).
     */
    private Set<ScaMethod> scaMethods;

    /**
     * Where to redirect user to. (For delayed redirection)
     */
    private String redirectTo;

    /**
     * Result body
     */
    private Object resultBody;

    public AuthStateBody(Set<ValidationError> violations) {
        this.violations = violations;
    }
}
