package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;


@Data
public class ValidatedPathHeadersBodyLog<P extends NotSensitiveData, H extends NotSensitiveData, B extends NotSensitiveData> implements NotSensitiveData {

    private P path;
    private H headers;
    private B body;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aValidatedPathHeadersBodyLog("
                + "path=" + this.getPath().getNotSensitiveData()
                + ", headers=" + this.getHeaders().getNotSensitiveData()
                + ", body=" + this.getBody().getNotSensitiveData()
                + ")";
    }
}
