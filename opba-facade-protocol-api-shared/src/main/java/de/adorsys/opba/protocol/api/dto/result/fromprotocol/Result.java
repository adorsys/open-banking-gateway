package de.adorsys.opba.protocol.api.dto.result.fromprotocol;

/**
 * Protocol result interface.
 * @param <T> Result body (i.e. account list)
 */
public interface Result<T> {

    /**
     * Non-sensitive information that can be persisted with authorization session and read on subsequent requests.
     * For example some internal ID, or protocol-encrypted data.
     */
    default String getAuthContext() {
        return null;
    }

    /**
     * Operation result body (i.e. account list).
     */
    default T getBody() {
        return null;
    }
}
