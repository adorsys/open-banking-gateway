package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.iban4j.Iban;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

@Data
@Validated
public class Transaction {

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    @NotBlank
    private String statementNumber;

    @NotBlank
    private String amount;

    @NotBlank
    private String balanceBefore;

    @NotBlank
    private String balanceAfter;

    @NotBlank
    @Length(max = 32)
    @SuppressWarnings("checkstyle:MagicNumber") // 32 is just maximum length
    private String toFirstLine;

    @NotBlank
    @Length(max = 32)
    @SuppressWarnings("checkstyle:MagicNumber") // 32 is just maximum length
    private String toSecondLine;

    @NotNull
    private Currency currency;

    @NotNull
    private String date;

    public String getDay() {
        return "" + LocalDateTime.parse(date).getDayOfYear();
    }

    public String getShortDate() {
        return LocalDateTime.parse(date).format(DateTimeFormatter.ofPattern("yyMMdd"));
    }

    public String getShortEntryDate() {
        return getShortDate();
    }

    public String getAmountWithComma() {
        if (amount.contains(".")) {
            return amount.replace(".", ",");
        }
        return amount + ",";
    }

    public String getTargetBlz() {
        return Iban.valueOf(to).getBankCode();
    }

    public String getTargetAccountNumber() {
        return Iban.valueOf(to).getAccountNumber();
    }
}
