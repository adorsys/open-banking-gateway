package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
class HbciStubGenerator {

    private static final Document SYNTAX = DocumentFactory.createDocument("300");
    private static final Set<String> NON_SENSITIVE_FIELDS = ImmutableSet.of(
            "BPD.BPA.KIK.country", "BPD.BPA.SegHead.version", "BPD.BPA.SuppLangs.lang", "BPD.BPA.SuppLangs.lang_2",
            "BPD.BPA.maxmsgsize", "BPD.BPA.version", "BPD.CommListRes.CommParam.addr", "BPD.CommListRes.KIK.blz",
            "BPD.CommListRes.SegHead.version", "BPD.CommListRes.deflan", "BPD.CommListRes.deflang",
            "BPD.Params.KUmsZeitPar5.ParKUmsZeit.timerange", "BPD.Params.KUmsZeitPar5.SegHead.code",
            "BPD.Params.KUmsZeitPar5.SegHead.version", "BPD.Params.KUmsZeitPar5.minsigs",
            "BPD.Params_*.AccInfoPar1.minsigs", "BPD.Params_*.ChangePINPar1.SegHead.code",
            "BPD.Params_*.ChangePINPar1.SegHead.version", "BPD.Params_*.ChangePINPar1.minsigs",
            "BPD.Params_*.DauerSEPADelPar1.ParDauerSEPADel.minpretime", "BPD.Params_*.DauerSEPADelPar1.SegHead.ref",
            "BPD.Params_*.DauerSEPADelPar1.maxnum",
            "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.execdayeditable",
            "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.recktoeditable",
            "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.timeuniteditable",
            "BPD.Params_*.DauerSEPAEditPar1.SegHead.ref", "BPD.Params_*.DauerSEPAEditPar1.SegHead.version",
            "BPD.Params_*.DauerSEPAEditPar1.maxnum", "BPD.Params_*.DauerSEPAEditPar1.minsigs",
            "BPD.Params_*.DauerSEPAEditPar1.secclass", "BPD.Params_*.DauerSEPAListPar1.SegHead.code",
            "BPD.Params_*.DauerSEPAListPar1.maxentries_allowed", "BPD.Params_*.DauerSEPAListPar1.secclass",
            "BPD.Params_*.DauerSEPANewPar1.SegHead.ref", "BPD.Params_*.DauerSEPANewPar1.SegHead.seq",
            "BPD.Params_*.DauerSEPANewPar1.SegHead.version", "BPD.Params_*.DauerSEPANewPar1.minsigs",
            "BPD.Params_*.KUmsZeitPar4.SegHead.code", "BPD.Params_*.KUmsZeitPar4.SegHead.ref",
            "BPD.Params_*.KUmsZeitPar4.SegHead.seq", "BPD.Params_*.KUmsZeitPar4.minsigs",
            "BPD.Params_*.PinTanPar2.ParPinTan.PinTanGV_*.needtan",
            "BPD.Params_*.PinTanPar2.ParPinTan.PinTanGV_*.segcode", "BPD.Params_*.PinTanPar2.ParPinTan.tanlen_max",
            "BPD.Params_*.PinTanPar2.minsigs", "BPD.Params_*.SEPAInfoPar1.ParSEPAInfo.cannationalacc",
            "BPD.Params_*.SEPAInfoPar1.ParSEPAInfo.canstructusage", "BPD.Params_*.SEPAInfoPar1.SegHead.ref",
            "BPD.Params_*.SEPAInfoPar1.SegHead.seq", "BPD.Params_*.SEPAInfoPar1.minsigs",
            "BPD.Params_*.SEPAInfoPar1.secclass", "BPD.Params_*.SaldoPar4.SegHead.ref",
            "BPD.Params_*.SaldoPar4.SegHead.version", "BPD.Params_*.SaldoPar4.maxnum",
            "BPD.Params_*.SaldoPar4.minsigs", "BPD.Params_*.SaldoPar5.SegHead.code",
            "BPD.Params_*.SaldoPar5.minsigs", "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.canstorno",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.initmode",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.name",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.needtanmedia",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.nofactivetanmedia",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.process",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.secfunc",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.zkamethod_version",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.canmultitan",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.canstorno",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.cantandelay",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.id",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.inputinfo",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.ischallengestructured",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.maxleninput2step",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.maxlentan2step",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.name",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.need_hhducresponse",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.needchallengeklass",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.needorderaccount",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.nofactivetanmedia",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.process",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.secfunc",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.tanformat",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.zkamethod_name",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_*.zkamethod_version",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.can1step", "BPD.Params_*.TAN2StepPar6.ParTAN2Step.orderhashmode",
            "BPD.Params_*.TAN2StepPar6.maxnum", "BPD.Params_*.Template2DPar.ParTemplate2D.dummy",
            "BPD.Params_*.Template2DPar.ParTemplate2D.dummy_5", "BPD.Params_*.Template2DPar.SegHead.code",
            "BPD.Params_*.Template2DPar.SegHead.ref", "BPD.Params_*.Template2DPar.SegHead.seq",
            "BPD.Params_*.Template2DPar.SegHead.version", "BPD.Params_*.Template2DPar.maxnum",
            "BPD.Params_*.TermUebSEPADelPar1.ParTermUebSEPADel.orderdata_required",
            "BPD.Params_*.TermUebSEPADelPar1.SegHead.seq", "BPD.Params_*.TermUebSEPADelPar1.maxnum",
            "BPD.Params_*.TermUebSEPAEditPar1.SegHead.code", "BPD.Params_*.TermUebSEPAEditPar1.SegHead.ref",
            "BPD.Params_*.TermUebSEPAEditPar1.SegHead.seq", "BPD.Params_*.TermUebSEPAEditPar1.minsigs",
            "BPD.Params_*.TermUebSEPAEditPar1.secclass",
            "BPD.Params_*.TermUebSEPAListPar1.ParTermUebSEPAList.cantimerange",
            "BPD.Params_*.TermUebSEPAListPar1.SegHead.seq", "BPD.Params_*.TermUebSEPAListPar1.secclass",
            "BPD.Params_*.TermUebSEPAPar1.SegHead.code", "BPD.Params_*.TermUebSEPAPar1.SegHead.ref",
            "BPD.Params_*.TermUebSEPAPar1.SegHead.seq", "BPD.Params_*.TermUebSEPAPar1.SegHead.version",
            "BPD.Params_*.TermUebSEPAPar1.maxnum", "BPD.Params_*.TermUebSEPAPar1.minsigs",
            "BPD.Params_*.UebSEPAPar1.SegHead.code", "BPD.Params_*.UebSEPAPar1.SegHead.ref",
            "BPD.Params_*.UebSEPAPar1.SegHead.seq", "BPD.Params_*.UmbPar1.SegHead.code",
            "BPD.Params_*.UmbPar1.SegHead.ref", "BPD.Params_*.UmbPar1.SegHead.seq", "BPD.Params_*.UmbPar1.minsigs",
            "BPD.SecMethod.SegHead.ref", "BPD.SecMethod.SegHead.seq", "BPD.SecMethod.SegHead.version",
            "GVRes.TAN2StepRes6.SegHead.code", "GVRes.TAN2StepRes6.SegHead.ref", "GVRes.TAN2StepRes6.SegHead.seq",
            "GVRes.TAN2StepRes6.SegHead.version", "GVRes.TAN2StepRes6.challenge", "GVRes.TAN2StepRes6.process",
            "GVRes.TAN2StepRes6.tanmedia", "Idn.KIK.blz", "Idn.KIK.country", "Idn.SegHead.code", "Idn.SegHead.seq",
            "Idn.SegHead.version", "Idn.sysStatus", "Idn.sysid", "MsgHead.MsgRef.msgnum", "MsgHead.SegHead.code",
            "MsgHead.SegHead.seq", "MsgHead.SegHead.version", "MsgHead.hbciversion", "MsgHead.msgnum",
            "MsgHead.msgsize", "MsgTail.SegHead.code", "MsgTail.SegHead.seq", "MsgTail.SegHead.version",
            "MsgTail.msgnum", "ProcPrep.BPD", "ProcPrep.SegHead.code", "ProcPrep.SegHead.seq",
            "ProcPrep.SegHead.version", "ProcPrep.UPD", "ProcPrep.lang", "ProcPrep.prodVersion",
            "RetGlob.RetVal.code", "RetGlob.RetVal_*.code", "RetGlob.SegHead.code", "RetGlob.SegHead.seq",
            "RetGlob.SegHead.version", "RetSeg.RetVal.code", "RetSeg.RetVal_*.code", "RetSeg.RetVal_*.parm",
            "RetSeg.RetVal_*.parm_2", "RetSeg.SegHead.code", "RetSeg.SegHead.ref", "RetSeg.SegHead.seq",
            "RetSeg.SegHead.versio", "RetSeg.SegHead.version", "RetSeg_*.RetVal.code", "RetSeg_*.SegHead.code",
            "RetSeg_*.SegHead.ref", "RetSeg_*.SegHead.seq", "RetSeg_*.SegHead.version", "SigTail.SegHead.code",
            "SigTail.SegHead.seq", "SigTail.SegHead.version", "TAN2Step6.SegHead.code", "TAN2Step6.SegHead.seq",
            "TAN2Step6.SegHead.version", "TAN2Step6.ordersegcode", "TAN2Step6.process", "TAN2StepRes6.SegHead.code",
            "TAN2StepRes6.SegHead.ref", "TAN2StepRes6.SegHead.seq", "TAN2StepRes6.SegHead.version",
            "TAN2StepRes6.challenge", "TAN2StepRes6.orderref", "TAN2StepRes6.process", "TAN2StepRes6.tanmedia",
            "UPD.KInfo.AllowedGV.code",
            "UPD.KInfo.AllowedGV.reqSigs", "UPD.KInfo.AllowedGV_*.code", "UPD.KInfo.AllowedGV_*.reqSigs",
            "UPD.KInfo.KTV.KIK.blz", "UPD.KInfo.KTV.KIK.country", "UPD.KInfo.SegHead.code", "UPD.KInfo.SegHead.ref",
            "UPD.KInfo.SegHead.seq", "UPD.KInfo.SegHead.version", "UPD.KInfo.UPD.KInfo.AllowedGV_*.reqSigs",
            "UPD.KInfo.cur", "UPD.KInfo_*.AllowedGV.code", "UPD.KInfo_*.AllowedGV.reqSigs",
            "UPD.KInfo_*.AllowedGV_*.code", "UPD.KInfo_*.AllowedGV_*.reqSigs", "UPD.KInfo_*.KTV.KIK.blz",
            "UPD.KInfo_*.KTV.KIK.country", "UPD.KInfo_*.SegHead.code", "UPD.KInfo_*.SegHead.ref",
            "UPD.KInfo_*.SegHead.seq", "UPD.KInfo_*.SegHead.version", "UPD.KInfo_*.acctype", "UPD.KInfo_*.cur",
            "UPD.KInfo_*.konto", "UPD.UPA.SegHead.code", "UPD.UPA.SegHead.ref", "UPD.UPA.SegHead.seq",
            "UPD.UPA.SegHead.version", "UPD.UPA.usage", "UPD.UPA.version",
            "BPD.Params_*.ChangePINPar1.SegHead.seq", "BPD.Params_*.SEPAInfoPar1.SegHead.code", "BPD.Params_*.DauerSEPADelPar1.SegHead.seq",
            "BPD.Params_*.CustomMsgPar4.SegHead.code", "BPD.Params_*.SaldoPar5.SegHead.seq", "BPD.Params_*.SaldoPar5.maxnum",
            "BPD.Params_*.UmbPar1.maxnum", "BPD.Params_*.KUmsZeitPar4.ParKUmsZeit.timerange", "BPD.Params_*.TermUebSEPADelPar1.SegHead.code",
            "BPD.Params_*.DauerSEPAListPar1.maxnum", "BPD.Params_*.DauerSEPADelPar1.ParDauerSEPADel.orderdata_required",
            "BPD.Params.KUmsZeitPar5.maxnum", "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.tanformat",
            "BPD.Params_*.TermUebSEPAPar1.ParTermUebSEPA.maxpretime", "BPD.Params_*.DauerSEPANewPar1.ParDauerSEPANew.maxpretime",
            "BPD.Params.KUmsZeitPar5.ParKUmsZeit.canallaccounts", "BPD.BPA.KIK.blz", "BPD.Params_*.DauerSEPANewPar1.ParDauerSEPANew.dayspermonth",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.canmultitan", "BPD.SecMethod.SegHead.code", "BPD.Params_*.UebSEPAPar1.minsigs",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.zkamethod_name", "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.maxpretime",
            "BPD.Params_*.Template2DPar.secclass", "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams.id", "BPD.Params_*.TAN2StepPar6.SegHead.version",
            "BPD.Params_*.ChangePINPar1.SegHead.ref", "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.usageeditable", "BPD.Params_*.DauerSEPAEditPar1.SegHead.seq",
            "BPD.Params_*.DauerSEPAEditPar1.ParDauerSEPAEdit.daysperweek", "BPD.CommListRes.SegHead.code",
            "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_4.initmode", "BPD.Params_*.TAN2StepPar6.ParTAN2Step.TAN2StepParams_3.needsmsaccount",
            "Sync.mode", "Sync.SegHead.seq", "Sync.SegHead.version", "Sync.SegHead.code", "TAN2Step6.notlasttan"
    ).stream().flatMap(it -> generateFromStarsRange100(it).stream()).collect(Collectors.toSet());

    private static final Set<String> SENSITIVE_FIELDS = ImmutableSet.of(
            "GVRes.TAN2StepRes6.orderref", "Idn.customerid", "MsgHead.MsgRef.dialogid", "MsgHead.dialogid",
            "ProcPrep.prodName", "RetGlob.RetVal.text", "RetGlob.RetVal_*.text", "RetSeg.RetVal.text",
            "RetSeg.RetVal_*.parm", "RetSeg.RetVal_*.text", "RetSeg_*.RetVal.text", "SigTail.seccheckref",
            "UPD.KInfo.KTV.number", "UPD.KInfo.accountdata", "UPD.KInfo.acctype", "UPD.KInfo.customerid",
            "UPD.KInfo.iban", "UPD.KInfo.konto", "UPD.KInfo.name1", "UPD.KInfo_*.KTV.number", "UPD.KInfo_*.acctype",
            "UPD.KInfo_*.customerid", "UPD.KInfo_*.iban", "UPD.KInfo_*.konto", "UPD.KInfo_*.name1",
            "UPD.UPA.userdata", "UPD.UPA.userid", "UPD.UPA.username", "BPD.Params_*.PinTanPar2.ParPinTan.info_customerid",
            "SigTail.UserSig.pin", "SigTail.UserSig.tan", "TAN2Step6.orderref"
    ).stream().flatMap(it -> generateFromStarsRange100(it).stream()).collect(Collectors.toSet());

    /**
     * This test takes HBCI dialog (multiple request-response) that may contain sensitive data and produces
     * safe version of it. Only HBCI tags (HNBNK, HNSHA...) and their order are kept and their parameters are replaced
     * with dummy ones.
     */
    @Test
    @SneakyThrows
    void generateImpersonatedStub() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/hbci-ag-mock/sparda/sync-my-temp.txt");
        Message data = message(
                new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                        .replaceAll("\n", "'")
                        .replace("'$", "")
        );
    }

    /**
     * This test simply classifies input message.
     */
    @Test
    @SneakyThrows
    void classifyMessage() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/hbci-ag-mock/sparda/sync-my-temp.txt");
        generateFromStarsRange100("UPD.KInfo.AllowedGV_*.code");

        classifyMessageType(
                new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                        .replaceAll("\n", "'")
                        .replace("'$", "")
        );
    }

    private static Set<String> generateFromStarsRange100(String str) {
        int stars = str.split("\\*", -1).length - 1;
        if (stars == 0) {
            return ImmutableSet.of(str);
        }

        Set<String> result = new LinkedHashSet<>();
        int starRange = 2;
        int max = BigDecimal.ONE.movePointRight(starRange * stars).intValueExact();
        for (int i = 0; i < max; i++) {
            String res = str;
            int value = i;
            for (int indStar = 0; indStar < stars; indStar++) {
                int div = BigDecimal.ONE.movePointRight(starRange * (stars - indStar - 1)).intValueExact();
                res = res.replaceFirst("\\*", "" + (value / div + 1));
                value -= (value / div) * div;
            }
            result.add(res);
        }

        return result;
    }

    private Message message(String from) {
        String type = classifyMessageType(from);
        return null;
    }

    private String classifyMessageType(String from) {
        NodeList list = SYNTAX.getElementsByTagName("MSGdef");
        AtomicReference<String> result = new AtomicReference<>();
        IntStream.range(0, list.getLength()).mapToObj(list::item)
                .map(it -> (Element) it)
                .forEach(node -> {
                    String msgName = node.getAttribute("id");
                    try {
                        Message msg = new Message(msgName, from, SYNTAX, false, true);
                        Set<String> keys = new HashSet<>(msg.getData().keySet());
                        int size = keys.size();
                        keys.removeAll(NON_SENSITIVE_FIELDS);
                        log.info("=================================== {} ===================================", msgName);
                        log.info("Found {} insensitive fields", size - keys.size());
                        size = keys.size();
                        keys.removeAll(SENSITIVE_FIELDS);
                        log.info("Found {} SENSITIVE fields", size - keys.size());
                        keys.forEach(it -> log.info("Found UNKNOWN FIELD: {}={}", it, msg.getData().get(it)));
                        log.info("============================================================================");
                    } catch (RuntimeException ex) {
                        //log.error("FAIL {}", msgName, ex);
                        // NOP
                    }
                });

        return result.get();
    }

    private Set<String> pathGroups(Message msg) {
        return null;
    }
}