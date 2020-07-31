package de.adorsys.opba.fintech.impl.config;

public enum Oauth2Provider {
    GMAIL;

    public static final String SEPARATOR = ":";

    public boolean matches(String encodedState) {
        String[] segments = encodedState.split(SEPARATOR);
        return segments.length > 1 && this.name().equals(segments[0]);
    }

    public String encode(String state) {
        return this.name() + SEPARATOR + state;
    }

    public String decode(String encodedState) {
        String[] segments = encodedState.split(SEPARATOR);
        if (!this.name().equals(segments[0])) {
            throw new IllegalStateException("Undecodeable state: " + encodedState);
        }

        return segments[1];
    }
}
