package de.adorsys.opba.protocol.api.dto.parameters;

/**
 * Additional Authorization request parameters used to extend {@link de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest}
 */
public enum ExtraAuthRequestParam {

    /**
     * Users' (PSU) login credentials at ASPSP.
     */
    PSU_ID,

    /**
     * PIN password / password used to login to ASPSP
     */
    PSU_PASSWORD,

    /**
     * Users' (PSU) IP address.
     */
    PSU_IP_ADDRESS;
}
