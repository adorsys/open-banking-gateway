package de.adorsys.opba.protocol.api.dto.codes;

/**
 * Where the {@link FieldCode} code should be applied to. For example there can be nested form that shows
 * IBAN input for consent object and Password input for authorization. To clearly identify where each field is
 * located - IBAN in consent, password in enveloping form, this class is used.
 */
public enum ScopeObject {

    /**
     * This field is located in general form (no preference).
     */
    GENERAL,

    /**
     * This field is inside AIS (Account Information Services) consent form. I.e. the form where user can select
     * what kind of consent it is - 'All accounts', 'All accounts with balances', etc.
     */
    AIS_CONSENT,

    /**
     * This field is inside AIS (Account Information Services) Consent Scope form. I.e. it is IBAN list of accounts
     * that are going to be available for this consent.
     */
    AIS_CONSENT_SCOPE,

    /**
     * This field is inside AIS (Account Information Services) consent form. I.e. the form where user can select
     * what kind of consent it is - 'All accounts', 'All accounts with balances', etc.
     */
    PIS_CONSENT
}
