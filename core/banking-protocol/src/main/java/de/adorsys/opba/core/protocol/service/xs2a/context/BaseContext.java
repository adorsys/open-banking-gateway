package de.adorsys.opba.core.protocol.service.xs2a.context;

import de.adorsys.opba.core.protocol.domain.dto.ValidationIssue;
import de.adorsys.opba.core.protocol.domain.entity.ProtocolAction;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BaseContext {

    private ContextMode mode;
    // Application required
    private long bankConfigId = 1;
    private ProtocolAction action;
    private String sagaId;

    private final Set<ValidationIssue> violations = new HashSet<>();
}
