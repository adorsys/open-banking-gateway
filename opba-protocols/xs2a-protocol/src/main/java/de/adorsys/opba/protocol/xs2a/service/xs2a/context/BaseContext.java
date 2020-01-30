package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.xs2a.domain.dto.ValidationIssue;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BaseContext {

    private ContextMode mode;
    // Application required
    private String aspspId;
    private ProtocolAction action;
    private String sagaId;

    private final Set<ValidationIssue> violations = new HashSet<>();

    public String getRequestId() {
        return this.sagaId;
    }
}
