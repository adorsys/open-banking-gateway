package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.xs2a.adapter.api.model.HrefType;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class StartScaprocessResponseLog {

        private ScaStatus scaStatus;
        private String authorisationId;
        private List<AuthenticationObjectLog> scaMethods;
        private AuthenticationObjectLog chosenScaMethod;
        private ChallengeDataLog challengeData;
        private Map<String, HrefType> links;
        private String psuMessage;
}
