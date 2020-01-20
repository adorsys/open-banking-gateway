package de.adorsys.opba.tppbanking.services.ais.account;

import lombok.Value;

import java.util.List;

@Value
public class AccountsReport {
    private final List<Account> accounts;
}
