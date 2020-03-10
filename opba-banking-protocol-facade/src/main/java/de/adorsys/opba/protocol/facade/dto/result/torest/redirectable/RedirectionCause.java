package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RedirectionCause {

    private ProtocolAction action;
    private Set<Cause> causes;
}
