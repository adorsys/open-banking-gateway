package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

/**
 * Data that is required from user to continue authorization.
 */
@Data
public class ValidationError {

    /**
     * Data type - i.e. Boolean,String, etc.
     */
    private String type;

    /**
     * Data scope - is general input, or input scoped to AIS consent object (like IBAN).
     */
    private String scope;

    /**
     * Data code to identify what is that (i.e. PSU_ID).
     */
    private String code;

    /**
     * Data description (caption for input).
     */
    private String captionMessage;
}
