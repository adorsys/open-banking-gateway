package de.adorsys.opba.protocol.services.ais.account;

import lombok.Data;

import java.util.Currency;

@Data
public class Account {
    private String resourceId;
    private String iban;
    private Currency currency;
    private String name;
    private String product;
    private String cashAccountType;
    private String status;
    private String linkedAccounts;
    private String usage;
}
