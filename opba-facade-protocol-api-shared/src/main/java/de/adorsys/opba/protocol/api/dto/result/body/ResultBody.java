package de.adorsys.opba.protocol.api.dto.result.body;

public interface ResultBody {

    default Object getBody() {
       return null;
    }
}
