package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {

    ANY("*"),
    DIALOG_INIT("DialogInit"),
    DIALOG_INIT_SCA_TAN_2_STEP("DialogInitScaTAN"),
    DIALOG_INIT_ANON("DialogInitAnon"),
    CUSTOM_MSG("CustomMsg"),
    SYNCH("Synch"),
    DIALOG_END("DialogEnd");

    private final String value;
}
