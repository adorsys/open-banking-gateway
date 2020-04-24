package de.adorsys.opba.protocol.api.dto.codes;

/**
 * Represents what kind of input is expected/provided from/by user. For example protocol may want users' PSU_ID
 * in order to create consent. This code is used to represent this.
 */
public enum FieldCode {
    /**
     * Null object.
     */
    NONE,

    /**
     * PSU login in ASPSP
     */
    PSU_ID,

    /**
     * IP address of PSU browser, mobile phone, etc.
     */
    PSU_IP_ADDRESS,

    /**
     * TAN password / password used to login to ASPSP
     */
    PSU_PASSWORD,

    /**
     * Whether the consent is for recurring action.
     */
    RECURRING_INDICATOR,

    /**
     * Date until when the consent will be valid.
     */
    VALID_UNTIL,

    /**
     * How frequently the consent can be used per day. For example 12 for list accounts means that FinTech
     * can retrieve user account list 12 times per day and after that authorization error will appear.
     */
    FREQUENCY_PER_DAY,

    /**
     * IBAN is required from user.
     */
    IBAN,

    /**
     * Transaction booking status is required.
     */
    BOOKING_STATUS,

    /**
     * Challenge result of 2-factor or multifactor authorization. I.e. SMS secret code from ASPSP to proceed with consent.
     */
    SCA_CHALLENGE_RESULT,

    /**
     * When multiple SCA challenges (2-factor or multifactor authorization) are available
     * (i.e. SMS from ASPSP, EMAIL from ASPSP) - the identifier of SCA challenge.
     */
    SCA_METHOD_ID
}
