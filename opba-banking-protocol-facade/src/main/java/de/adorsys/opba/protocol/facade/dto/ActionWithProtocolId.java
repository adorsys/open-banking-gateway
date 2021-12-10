package de.adorsys.opba.protocol.facade.dto;

import lombok.Data;

/**
 * Action to be executed within protocol.
 * @param <ACTION> Action to execute
 */
@Data
public class ActionWithProtocolId<ACTION>  {

    private final long bankActionId;
    private final ACTION action;
}
