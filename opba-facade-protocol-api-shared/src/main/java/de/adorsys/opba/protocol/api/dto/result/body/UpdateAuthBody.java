package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

@Data
public class UpdateAuthBody implements ResultBody {

    // FIXME add type-type mapping
    private Object auth;
}
