package de.adorsys.opba.protocol.api.dto.codes;

/**
 * Represents the type of users' input that is requested from user. Used primarily for rendering optimization.
 */
public enum TypeCode {

    /**
     * Composite object that is composed of multiple primitives is required from user.
     */
    OBJECT,

    /**
     * Boolean (true/false/null) is required from user.
     */
    BOOLEAN,

    /**
     * String value is required from user.
     */
    STRING,

    /**
     * Signed integer value is required from user.
     */
    INTEGER,

    /**
     * Date in ISO-8601 format is required from user.
     */
    DATE,

    /**
     * UI can't show this field, throws runtime exception if occurred.
     */
    PROHIBITED
}
