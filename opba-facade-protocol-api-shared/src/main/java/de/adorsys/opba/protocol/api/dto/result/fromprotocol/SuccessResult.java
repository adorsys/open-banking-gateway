package de.adorsys.opba.protocol.api.dto.result.fromprotocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResult<T> implements Result<T> {

    private T body;
}
