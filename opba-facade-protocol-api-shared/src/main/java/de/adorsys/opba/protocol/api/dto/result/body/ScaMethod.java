package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SCA method description (i.e. SMS/email 2FA method).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
