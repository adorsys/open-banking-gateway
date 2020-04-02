package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class WiremockConst {

    public static final String ANTON_BRUECKNER_RESOURCE_ID = "cmD4EYZeTkkhxRuIV1diKA";
    public static final String MAX_MUSTERMAN_RESOURCE_ID = "oN7KTVuJSVotMvPPPavhVo";
    public static final LocalDate DATE_FROM = LocalDate.parse("2018-01-01");
    public static final LocalDate DATE_TO = LocalDate.parse("2020-09-30");
    public static final String BOTH_BOOKING = "BOTH";
}
