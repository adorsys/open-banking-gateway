package de.adorsys.opba.protocol.facade.config.encryption;

public interface SymmetricEncSpec {

    String getKeyAlgo();
    String getCipherAlgo();
    int getIvSize();
    int getLen();
}
