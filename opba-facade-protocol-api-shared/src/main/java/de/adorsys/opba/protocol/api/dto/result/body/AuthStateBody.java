package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.common.SupportedConsentType;
import de.adorsys.opba.protocol.api.dto.request.ChallengeData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * State of current authorization - for example PSU ID required from user.
 */
@Data
@AllArgsConstructor
public class AuthStateBody implements ResultBody {

    /**
     * Current protocol action - i.e. list accounts or list transactions.
     * See {@code de.adorsys.opba.consentapi.model.generated.ActionEnum}
     */
    private String action;

    /**
     * What inputs are required from user - i.e. PSU ID.
     */
    private Set<ValidationError> violations;

    /**
     * The type of the consent supported by ASPSP.
     * See {@code de.adorsys.opba.protocol.api.common.SupportedConsentType}
     */
    private List<SupportedConsentType> supportedConsentTypes;

    /**
     * Which SCA methods are available for consent authorization (i.e. SMS, email).
     */
    private Set<ScaMethod> scaMethods;

    /**
     * Where to redirect user to. (For delayed redirection)
     */
    private String redirectTo;

    /**
     * Authorization request data - describes what data was requested or is being requested (by FinTech or is current):
     * Consent object, Payment, which ASPSP or FinTech.
     */
    private AuthRequestData requestData;

    /**
     * Challenge data, needed for embedded SCA for OpticTAN, PhotoTAN and other challenges that require some data to be shown to user
     */
    private ChallengeData challengeData;

    public AuthStateBody(Set<ValidationError> violations) {
        this.violations = violations;
    }
}
