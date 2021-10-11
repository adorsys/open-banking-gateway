package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;


@Data
public class ValidatedQueryHeadersLog<Q extends NotSensitiveData, H extends NotSensitiveData> {

    private Q query;
    private H headers;

    public String getNotSensitiveData() {
        return "Xs2aValidatedPathHeadersBodyLog("
                + "query=" + this.getQuery().getNotSensitiveData()
                + ", headers=" + this.getHeaders().getNotSensitiveData()
                + ")";
    }
}
