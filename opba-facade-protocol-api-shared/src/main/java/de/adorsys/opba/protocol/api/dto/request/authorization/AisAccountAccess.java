package de.adorsys.opba.protocol.api.dto.request.authorization;

import lombok.Data;

import java.util.List;

@Data
public class AisAccountAccess {

    private List<String> accounts;
    private List<String> balances;
    private List<String> transactions;

    private String allPsd2;
    private String availableAccounts;
}
