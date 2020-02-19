package de.adorsys.opba.protocol.api.dto.result.body;

import java.util.Arrays;

public interface ResultBody {

    default Object getBody() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(new RuntimeException().getStackTrace()).forEach(el -> sb.append(el.toString()));
        System.err.println("SYSTEM ERR:" + sb.toString());
        throw new RuntimeException("NYI with stack " + sb.toString());
    }
}
