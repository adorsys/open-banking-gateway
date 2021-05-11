package de.adorsys.opba.protocol.facade.util.logresolver.domain;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.context.ServiceContextLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.facade.util.logresolver.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class ProtocolWithCtxLog<REQUEST extends FacadeServiceableGetter> {

    private final ActionLog protocol;
    private final ServiceContextLog<REQUEST> context;

    public String getNotSensitiveData() {

        return "ProtocolWithCtxLog("
                + "protocol=" + (null != protocol ? protocol.toString() : NULL)
                + ", context=" + (null != context ? context.getNotSensitiveData() : NULL)
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {

        return "ServiceContextLog{"
                + "protocol=" + protocol.toString()
                + ", context=" + context.toString()
                + '}';
    }
}
