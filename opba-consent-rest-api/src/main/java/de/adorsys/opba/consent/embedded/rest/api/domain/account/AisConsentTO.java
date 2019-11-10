/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.opba.consent.embedded.rest.api.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Ais consent request", value = "AisConsentRequest")
public class AisConsentTO {

    @ApiModelProperty(value = "The consent id", required = true)
    private String id;

    @ApiModelProperty(value = "Corresponding PSU", required = true)
    private String userId;

    @ApiModelProperty(value = "ID of the corresponding TPP.", required = true, example = "testTPP")
    private String tppId;

    @ApiModelProperty(value = "Maximum frequency for an access per day. For a once-off access, this attribute is set to 1", required = true, example = "4")
    private int frequencyPerDay;

    @ApiModelProperty(value = "Set of accesses given by psu for this account", required = true)
    private AisAccountAccessInfoTO access;

    @ApiModelProperty(value = "Consent`s expiration date. The content is the local ASPSP date in ISODate Format", required = true, example = "2020-10-10")
    private LocalDate validUntil;

    @ApiModelProperty(value = "'true', if the consent is for recurring access to the account data , 'false', if the consent is for one access to the account data", required = true, example = "false")
    private boolean recurringIndicator;

    @JsonIgnore
    public boolean isValidConsent() {
        return validUntil == null || validUntil.isAfter(LocalDate.now());
    }
}
