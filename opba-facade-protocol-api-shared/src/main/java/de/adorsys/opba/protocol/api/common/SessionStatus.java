package de.adorsys.opba.protocol.api.common;

/**
 * Describes different states for Session (Service/Authorization).
 */
public enum SessionStatus {

    /**
     * Session created, but user(s) haven't started its authorization in OBG.
     */
    PENDING,

    /**
     * User(s) started session authorization in OBG.
     */
    STARTED,

    /**
     * User(s) successfully completed session authorization in OBG. Note it is not yet ready to use and must be activated.
     */
    COMPLETED,

    /**
     * Session is activated and is ready to use.
     */
    ACTIVATED,

    /**
     * Session authorization was denied.
     */
    DENIED,

    /**
     * Session authorization is failed.
     */
    ERROR,
}
