package de.adorsys.opba.consent.rest.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Denies a redirect to ASPSP requested by the ConsentAuthorisationApi")
public class DenyRedirectRequest extends AuthorizeRequest {
	
	@ApiModelProperty("In case there is no redirect back to TPP desired, exit page can be specified by ConsentAuthorisationApi")
	private String exitPage;
	
	@ApiModelProperty("Will indicate if PSU wants to be sent back to FinTechApi.")
	private boolean backToFinTech;

    @ApiModelProperty(value = "Set to true if consent object shall be forgotten or frozen.", required = false)
    private Boolean forgetConsent;
}
