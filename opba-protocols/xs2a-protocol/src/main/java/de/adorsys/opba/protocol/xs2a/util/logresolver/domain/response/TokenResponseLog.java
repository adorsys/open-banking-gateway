package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;


@Data
public class TokenResponseLog implements NotSensitiveData {

    private String accessToken;
    private String tokenType;
    private Long expiresInSeconds;
    private String refreshToken;
    private String scope;

    @Override
    public String getNotSensitiveData() {
        return "TokenResponseLog("
                + "tokenType=" + this.getTokenType()
                + ")";
    }
}
