package de.adorsys.opba.consent.rest.api.domain;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmountTOTest {

    @Test
    public void testToCheckCodeCoverage() {
        AmountTO amountTO = new AmountTO(Currency.getInstance("EUR"), new BigDecimal(20));
        log.debug(amountTO.getAmount().toString());
        log.debug(amountTO.getCurrency().toString());
    }
}
