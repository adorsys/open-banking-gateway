package de.adorsys.opba.consent.rest.api.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import de.adorsys.opba.consent.rest.api.domain.AmountTO;

import java.math.BigDecimal;
import java.util.Currency;

@Slf4j
public class AmountTOTest {

    @Test
    public void testToCheckCodeCoverage() {
        AmountTO amountTO = new AmountTO(Currency.getInstance("EUR"), new BigDecimal(20));
        log.debug(amountTO.getAmount().toString());
        log.debug(amountTO.getCurrency().toString());
    }
}
