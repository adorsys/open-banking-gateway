package de.adorsys.opba.protocol.facade.config.encryption;

public interface EncSpec {

    String getKeyAlgo();
    String getCipherAlgo();
    int getIvSize();
    int getLen();
}
