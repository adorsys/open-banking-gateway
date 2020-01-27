package de.adorsys.opba.core.protocol.service.xs2a.context;

import de.adorsys.opba.core.protocol.domain.dto.forms.ScaMethod;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateBody;
import de.adorsys.xs2a.adapter.service.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aContext extends BaseContext {

    // Mandatory static
    private String psuId;
    private String contentType = "application/json";
    private String aspspId;
    private ConsentInitiateBody consent = new ConsentInitiateBody(); // to avoid initialization in more-parameters

    // Mandatory dynamic
    private String psuIpAddress;

    // Optional consent-specific
    private Boolean withBalance;

    // In-process
    private String aspspScaApproach;
    private StartScaProcessResponse startScaProcessResponse;
    private String consentId;
    private String authorizationId;
    private String scaStatus;
    private List<ScaMethod> availableSca;
    private String userSelectScaId;
    private AuthenticationObject scaSelected;
    private String lastScaChallenge;

    // sensitive - do not persist?
    private String psuPassword;

    // TODO: protect from overriding using reflection
    private String redirectUriOk;

    // TODO: protect from overriding reflection
    private String redirectUriNok;
}
