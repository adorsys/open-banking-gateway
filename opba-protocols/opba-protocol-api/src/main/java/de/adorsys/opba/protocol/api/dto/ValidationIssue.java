package de.adorsys.opba.protocol.api.dto;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.ScopeObject;
import de.adorsys.opba.protocol.api.dto.codes.TypeCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode(of = {"code", "scope"})
public class ValidationIssue {

    @NonNull
    private final TypeCode type;

    @NonNull
    private final ScopeObject scope;

    @NonNull
    private final FieldCode code;

    private final String captionMessage;

    @Override
    public String toString() {
        return "{"
                + "\"type\":\"" + type + "\""
                + ", \"scope\":\"" + scope + "\""
                + ", \"code\":\"" + code + "\""
                + ", \"captionMessage\":\"" + captionMessage + "\""
                + "}";
    }
}
