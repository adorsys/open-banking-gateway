package de.adorsys.opba.protocol.facade.util.logresolver.domain;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.api.Constants.NULL;


@RequiredArgsConstructor
public class ActionLog<REQUEST extends FacadeServiceableGetter, RESULT extends ResultBody, ACTION extends Action<REQUEST, RESULT>> {

    private final ACTION action;

    @SneakyThrows
    @Override
    public String toString() {
        if (null == action) {
            return NULL;
        }

        return "ActionLog{"
                + "action=" + action.getClass()
                + '}';
    }
}
