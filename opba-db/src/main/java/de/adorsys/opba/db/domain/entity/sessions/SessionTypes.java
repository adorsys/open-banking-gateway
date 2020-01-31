package de.adorsys.opba.db.domain.entity.sessions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SessionTypes {

    SERVICE(Values.SERVICE),
    AUTHENTICATION(Values.AUTHENTICATION);

    private final String value;

    public static class Values {
        public static final String SERVICE = "1";
        public static final String AUTHENTICATION = "2";
    }
}
