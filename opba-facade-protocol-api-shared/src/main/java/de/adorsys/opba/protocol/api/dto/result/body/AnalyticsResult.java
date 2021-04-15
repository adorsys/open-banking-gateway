package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class AnalyticsResult {

    // ******** categorization provided fields **********
    private String mainCategory;
    private String subCategory;
    private String specification;
    private String otherAccount;
    private String logo;
    private String homepage;
    private String hotline;
    private String email;
    private Map<String, String> custom;
    private Set<String> usedRules = new HashSet<>();
    // *****************************************

    // ******** classification provided fields *********
    private LocalDate nextBookingDate;
    private String cycle;
    // *****************************************
}
