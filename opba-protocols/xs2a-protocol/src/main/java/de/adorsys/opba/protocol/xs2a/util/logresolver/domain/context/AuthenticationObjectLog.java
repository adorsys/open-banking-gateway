package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.xs2a.adapter.api.model.AuthenticationType;
import lombok.Data;


@Data
public class AuthenticationObjectLog {

        private AuthenticationType authenticationType;
        private String authenticationVersion;
        private String authenticationMethodId;
        private String name;
        private String explanation;

}
