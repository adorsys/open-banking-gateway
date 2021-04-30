package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import lombok.Data;


@Data
public class ValidatedPathHeadersLog<P extends NotSensitiveData, H extends NotSensitiveData> implements NotSensitiveData {

    private P path;
    private H headers;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aValidatedPathHeadersBodyLog("
                + "path=" + this.getPath().getNotSensitiveData()
                + ", headers=" + this.getHeaders().getNotSensitiveData()
                + ")";
    }
}
