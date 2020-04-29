package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

@Data
public class Address {
    private String streetName;
    private String buildingNumber;
    private String city;
    private String postCode;
    private String country;
}
