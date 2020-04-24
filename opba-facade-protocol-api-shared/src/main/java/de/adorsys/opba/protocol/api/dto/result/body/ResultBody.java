package de.adorsys.opba.protocol.api.dto.result.body;

/**
 * Interface that represents protocol result to be processed by Facade.
 */
public interface ResultBody {

    /**
     * Body of the results - i.e. account list.
     */
    default Object getBody() {
       return null;
    }
}
