package de.adorsys.opba.protocol.api.dto.result.body;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface that represents protocol result to be processed by Facade.
 */
public interface ResultBody {

    /**
     * Body of the results - i.e. account list.
     */
    @JsonIgnore
    default Object getBody() {
       return null;
    }
}
