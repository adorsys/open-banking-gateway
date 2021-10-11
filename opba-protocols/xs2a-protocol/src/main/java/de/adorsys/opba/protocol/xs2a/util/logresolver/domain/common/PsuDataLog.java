package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import lombok.Data;


@Data
public class PsuDataLog {

    private String password;
    private String encryptedPassword;
    private String additionalPassword;
    private String additionalEncryptedPassword;

}
