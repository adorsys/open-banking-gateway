package de.adorsys.opba.protocol.facade.dto;

import lombok.Data;

@Data
public class ActionWithProtocolId<A>  {

    private final long bankActionId;
    private final A action;
}
