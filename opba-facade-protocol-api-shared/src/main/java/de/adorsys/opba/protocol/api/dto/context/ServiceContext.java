package de.adorsys.opba.protocol.api.dto.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceContext<T> {

    private Long bankProtocolId;
    private String bankId;

    @NonNull
    private UUID serviceSessionId;

    private UUID authSessionId;

    @NonNull
    private T request;

    private String authContext;
}
