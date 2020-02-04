package de.adorsys.opba.protocol.api.dto.result.body;

public interface ResultBody {

    // FIXME add type-type mapping
    default Object getBody() {
        return null;
    }
}
