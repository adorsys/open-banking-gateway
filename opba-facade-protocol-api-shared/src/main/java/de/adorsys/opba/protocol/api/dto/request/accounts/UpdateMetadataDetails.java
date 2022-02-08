package de.adorsys.opba.protocol.api.dto.request.accounts;

import lombok.Data;

/**
 * Update 3rd party connection metadata request body.
 */
@Data
public class UpdateMetadataDetails {

    /**
     * body content for 3rd party service. For example:
     * {"ipAddress": "1.1.1.1", "deviceOs": "Macintosh; Intel Mac OS X 10_15_7", "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko)"}
     */
    private String adapterProperties;
}
