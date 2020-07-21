package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class Const {

    public static final String CONTEXT = "CONTEXT";

    public static final String SEPA_INFO = "GV.SEPAInfo";
    public static final String TRANSACTIONS = "GV.KUmsZeit";
    public static final String PAYMENT = "GV.UebSEPA1";
    public static final String DIALOG_ID = "MsgHead.dialogid";
}
