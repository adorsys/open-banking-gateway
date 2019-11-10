package de.adorsys.opba.consent.embedded.rest.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PsuAuthDataTO {

    private String psuId;

    private String psuCorporateId;

    @ApiModelProperty(value = "Password", required = true)
    private String password;
}
