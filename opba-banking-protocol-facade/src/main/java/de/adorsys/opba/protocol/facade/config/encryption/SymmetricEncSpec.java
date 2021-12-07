package de.adorsys.opba.protocol.facade.config.encryption;

/**
 * Symmetric encryption specification.
 */
public interface SymmetricEncSpec {

    /**
     * Key algorithm
     */
    String getKeyAlgo();

    /**
     * Cipher algorithm
     */
    String getCipherAlgo();

    /**
     * Initialization vector size, bytes.
     */
    int getIvSize();

    /**
     * Key length, bits.
     */
    int getLen();
}
