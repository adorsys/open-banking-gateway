package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.multibanking.domain.BankAccount;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HbciUtil {

    public BankAccount buildBankAccount(String iban) {
        BankAccount account = new BankAccount();
        account.setIban(iban);
        return account;
    }
}
