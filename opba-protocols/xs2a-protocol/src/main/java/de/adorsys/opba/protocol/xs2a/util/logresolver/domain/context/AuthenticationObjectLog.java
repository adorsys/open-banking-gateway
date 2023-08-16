package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import lombok.Data;


@Data
public class AuthenticationObjectLog {

        private String authenticationType;
        private String authenticationVersion;
        private String authenticationMethodId;
        private String name;
        private String explanation;

}
