package de.adorsys.opba.protocol.bpmnshared.service;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class TransactionUtil {

    public boolean isWithinRange(LocalDate transactionDate, LocalDate from, LocalDate to) {
        if (null == transactionDate) {
            return true;
        }

        from = null == from ? LocalDate.MIN : from;
        to = null == to ? LocalDate.MAX : to;

        return transactionDate.isAfter(from) && transactionDate.isBefore(to);
    }
}
