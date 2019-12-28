package de.adorsys.opba.core.protocol.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class ContextParamUpdate {

    @NotEmpty
    private Map<@NotBlank String, String> updates;
}
