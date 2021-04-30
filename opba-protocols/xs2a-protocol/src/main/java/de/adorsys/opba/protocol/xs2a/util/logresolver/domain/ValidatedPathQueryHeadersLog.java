package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import lombok.Data;


@Data
public class ValidatedPathQueryHeadersLog<P extends NotSensitiveData, Q extends NotSensitiveData, H extends NotSensitiveData> implements NotSensitiveData {

    private P path;
    private Q query;
    private H headers;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aValidatedPathQueryHeadersLog("
                + "path=" + this.getPath().getNotSensitiveData()
                + ", query=" + this.getQuery().getNotSensitiveData()
                + ", headers=" + this.getHeaders().getNotSensitiveData()
                + ")";
    }
}
