package de.adorsys.opba.fireflyexporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportableAccount {

    private String iban;
    private String resourceId;
}
