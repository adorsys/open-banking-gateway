package de.adorsys.opba.core.protocol.domain.dto.forms;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ScaChallengeResult {

    @NotBlank
    private String scaChallengeResult;
}
