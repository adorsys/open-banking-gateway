package de.adorsys.opba.consent.rest.api.domain;

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PsuAuthRequest extends AuthorizeRequest {

  @ApiModelProperty(value = "SCA authentication data, depending on the chosen authentication method. If the data is"
      + " binary, then it is base64 encoded.", required = true)
  private Map<String, String> scaAuthenticationData;
}
