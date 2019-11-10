package de.adorsys.opba.consent.embedded.rest.api.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressTO {
    private String street;
    private String buildingNumber;
    private String city;
    private String postalCode;
    private String country;
}
