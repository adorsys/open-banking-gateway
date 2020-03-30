package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
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

    // Mandatory dynamic
    private String psuIpAddress; // FIXME https://github.com/adorsys/open-banking-gateway/issues/251

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

    // TODO: protect from overriding using reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriOk;

    // TODO: protect from overriding reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriNok;

    private String fintechRedirectUriOk;
    private String fintechRedirectUriNok;

    private boolean redirectConsentOk;
}
