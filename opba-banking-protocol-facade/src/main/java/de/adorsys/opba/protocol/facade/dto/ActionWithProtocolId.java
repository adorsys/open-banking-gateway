package de.adorsys.opba.protocol.facade.dto;

import lombok.Data;

@Data
public class ActionWithProtocolId<ACTION>  {

    private final long bankActionId;
    private final ACTION action;
}
