package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to be very specific of what to save.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastViolations {

    private Set<ValidationIssue> violations = new HashSet<>();
}
