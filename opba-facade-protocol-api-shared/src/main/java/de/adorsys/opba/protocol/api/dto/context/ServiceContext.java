package de.adorsys.opba.protocol.api.dto.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceContext<T> {

    private T request;
    private int bankProtocolId;
}
