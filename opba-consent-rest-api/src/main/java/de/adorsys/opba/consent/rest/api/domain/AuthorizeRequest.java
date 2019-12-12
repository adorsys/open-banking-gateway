package de.adorsys.opba.consent.rest.api.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "Contains information used to legitimate a request.")
public class AuthorizeRequest extends ConsentAuthTO {
}
