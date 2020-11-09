package de.adorsys.opba.protocol.api.dto.result.fromprotocol;

/**
 * Protocol result interface.
 *
 * @param <T> Result body (i.e. account list)
 */
public interface Result<T> {

    /**
     * Non-sensitive information that can be persisted with authorizaiton session and read on subsequent requests.
     * For example some internal ID.
     */
    default String authContext() {
        return null;
    }

    /**
     * Operation result body (i.e. account list).
     */
    default T getBody() {
        return null;
    }

    /**
     * Keeps session key, if this method returns true.
     */
    default boolean doNotRemoveKey() {
        return false;
    }
}
