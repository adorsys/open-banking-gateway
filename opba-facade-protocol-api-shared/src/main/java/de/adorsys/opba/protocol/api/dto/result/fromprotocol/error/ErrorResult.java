package de.adorsys.opba.protocol.api.dto.result.fromprotocol.error;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.Data;

@Data
public class ErrorResult<T> implements Result<T> {

    private String message;
    private String code;
}
