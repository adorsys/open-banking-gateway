package de.adorsys.opba.consent.embedded.rest.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PsuScaDoneDataTO {
    @ApiModelProperty(value = "Set to true if consent object shall be forgotten or frozen.", required = false)
    private Boolean forgetConsent;

    @ApiModelProperty(value = "Set to true if PSU shall be redirected to the TPP, "
    		+ "false if redirect information shall be returned in the response body. "
    		+ "Null if no redirect information needed.",required = true)
    private Boolean backToTpp;
}
