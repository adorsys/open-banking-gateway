package de.adorsys.opba.protocol.facade.config.encryption;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface CmsEncSpec {

    String getKeyAlgo();
    int getLen();
    ASN1ObjectIdentifier getCipherAlgo();
}
