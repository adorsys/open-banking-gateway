package de.adorsys.opba.protocol.api.services;

/**
 * Encryption service to store intermediate data. Is essential for proper protocol implementation as intermediate
 * data may include consent ID, IBAN list, etc. that should be protected.
 */
public interface EncryptionService {

    /**
     * @return Encryption key ID that is used to protect the data.
     */
    String getEncryptionKeyId();

    /**
     * Encrypt data using underlying encryption key.
     * @param data data to encrypt
     * @return encrypted data
     */
    byte[] encrypt(byte[] data);

    /**
     * Decrypt data using underlying encryption key.
     * @param data data to decrypt
     * @return decrypted data
     */
    byte[] decrypt(byte[] data);
}
