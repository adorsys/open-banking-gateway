package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.xs2a.adapter.api.model.Address;
import lombok.Data;


@Data
public class AddressLog extends Address {

    private String streetName;
    private String buildingNumber;
    private String townName;
    private String postCode;
    private String country;

}
