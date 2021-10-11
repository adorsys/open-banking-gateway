package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.PSU_PASSWORD;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.PSU_ID;

import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;

@Data
public class ProvidePsuIdAndPsuPasswordBody {

    /**
     * PSU PIN/password to call XS2A api with.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(PSU_PASSWORD))
    @NotBlank(message = "{no.psu.password}")
    private String psuPassword;

    /**
     * PSU PIN/password to call XS2A api with.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(PSU_ID))
    @NotBlank(message = "{no.psu.id}")
    private String psuId;



}
