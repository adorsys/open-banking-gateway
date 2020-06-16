package de.adorsys.opba.protocol.hbci.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents all current context violations that should be fixed by the user providing relevant parameters.
 * Helper class to be very specific of what to save.
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
// TODO - decide do we need to encrypt these
public class LastViolations implements UsesRequestScoped, RequestScoped {

    private Set<ValidationIssue> violations = new HashSet<>();

    public LastViolations(Set<ValidationIssue> violations) {
        this.violations = violations;
    }

    /**
     * Request scoped services provider for sensitive data.
     */
    @Delegate
    @JsonIgnore
    private RequestScoped requestScoped;
}
