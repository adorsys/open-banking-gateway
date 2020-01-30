package de.adorsys.opba.protocol.api.dto.result;

import lombok.Data;

@Data
public class SuccessResult<T> implements Result<T> {

    private T body;
}
