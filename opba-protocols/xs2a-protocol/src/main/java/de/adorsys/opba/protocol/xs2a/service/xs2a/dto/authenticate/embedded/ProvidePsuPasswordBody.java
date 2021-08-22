package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.api.model.PsuData;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthentication;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.PSU_PASSWORD;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;

/**
 * Mapper from {@link Xs2aContext} to {@link UpdatePsuAuthentication} object to pass password/TAN to XS2A adapter.
 */
@Data
public class ProvidePsuPasswordBody {

    /**
     * PSU PIN/password to call XS2A api with.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(PSU_PASSWORD))
    @NotBlank(message = "{no.psu.password}")
    private String psuPassword;

    private  boolean passwordShouldBeEncrypted;

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> {

       default UpdatePsuAuthentication map(ProvidePsuPasswordBody cons) {
            PsuData psuData = new PsuData();
            if (cons.isPasswordShouldBeEncrypted()) {
                psuData.setEncryptedPassword(cons.getPsuPassword());
            } else {
                psuData.setPassword(cons.getPsuPassword());
            }
            UpdatePsuAuthentication updatePsuAuthentication = new UpdatePsuAuthentication();
            updatePsuAuthentication.setPsuData(psuData);
            return updatePsuAuthentication;
        }
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, ProvidePsuPasswordBody> {
        ProvidePsuPasswordBody map(Xs2aContext ctx);
    }
}
