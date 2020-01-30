package de.adorsys.opba.db.domain.entity.sessions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SessionTypes {

    SERVICE(Values.SERVICE),
    VALIDATION(Values.VALIDATION),
    AUTHENTICATION(Values.AUTHENTICATION);

    private final String value;

    public static class Values {
        public static final String SERVICE = "1";
        public static final String VALIDATION = "2";
        public static final String AUTHENTICATION = "3";
    }
}
