package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

/**
 * SCA method description (i.e. SMS/email 2FA method).
 */
@Data
public class ScaMethod {

    /**
     * SCA method ID.
     */
    private String key;

    /**
     * SCA method name (caption).
     */
    private String value;
}
