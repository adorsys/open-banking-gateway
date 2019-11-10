package de.adorsys.opba.consent.embedded.rest.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectedPsuAuthMethodTO {
    @ApiModelProperty(value = "An identification provided by the ASPSP for the later identification of the " +
        "authentication method selection.",
        required = true,
        example = "myAuthenticationID"
    )
    private String authenticationMethodId;
}
