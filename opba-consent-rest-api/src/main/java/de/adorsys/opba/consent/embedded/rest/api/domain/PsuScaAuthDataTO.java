package de.adorsys.opba.consent.embedded.rest.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PsuScaAuthDataTO {
    @ApiModelProperty(value = "SCA authentication data, depending on the chosen authentication method. If the data is" +
            " binary, then it is base64 encoded.",
            required = true)
        private String scaAuthenticationData;
}
