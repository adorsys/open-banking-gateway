package de.adorsys.opba.protocol.api.dto;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.ScopeObject;
import de.adorsys.opba.protocol.api.dto.codes.TypeCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * The object represents that to continue with i.e. Consent authorization, certain input from user is needed
 * (i.e. his ASPSP login). This object envelopes the requirements to user input.
 */
@Getter
@Builder
@EqualsAndHashCode(of = {"code", "scope"})
public class ValidationIssue {

    /**
     * The users' requested input type like Date, String, etc.
     */
    @NonNull
    private final TypeCode type;

    /**
     * The users' requested input scope - like the input belongs to AIS consent specification object and not to
     * general form.
     */
    @NonNull
    private final ScopeObject scope;

    /**
     * Field code - used to identify what is required from the user. For example PSU_ID dictates that we need
     * users ASPSP login.
     */
    @NonNull
    private final FieldCode code;

    /**
     * Optional message associated with the field. Probably will be removed.
     */
    private final String captionMessage;

    /**
     * @return JSON representation of current object.
     */
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
