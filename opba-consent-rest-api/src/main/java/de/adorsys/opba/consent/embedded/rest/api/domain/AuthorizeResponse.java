package de.adorsys.opba.consent.embedded.rest.api.domain;

import java.util.List;

public class AuthorizeResponse extends PsuMessageBearingResponse {
    /*
     * The id of the business process, login, payment, consent.
     */
    private String encryptedConsentId;

    private List<ScaUserDataTO> scaMethods;

    /*
     * The id of this authorisation instance.
     */
    private String authorisationId;

    /*
     * The sca status is used to manage authorisation flows.
     */
    private ScaStatusTO scaStatus;

    public String getEncryptedConsentId() {
        return encryptedConsentId;
    }

    public void setEncryptedConsentId(String encryptedConsentId) {
        this.encryptedConsentId = encryptedConsentId;
    }

    public List<ScaUserDataTO> getScaMethods() {
        return scaMethods;
    }

    public void setScaMethods(List<ScaUserDataTO> scaMethods) {
        this.scaMethods = scaMethods;
    }

    public ScaStatusTO getScaStatus() {
        return scaStatus;
    }

    public void setScaStatus(ScaStatusTO scaStatus) {
        this.scaStatus = scaStatus;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

}
