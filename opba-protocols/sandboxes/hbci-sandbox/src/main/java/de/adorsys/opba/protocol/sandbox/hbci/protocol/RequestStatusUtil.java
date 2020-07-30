package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class RequestStatusUtil {

    public boolean isForTransactionListing(Map<String, String> requestData) {
        return "HKKAZ".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.ordersegcode"));
    }

    public boolean isForAccountListing(Map<String, String> requestData) {
        return "HKSPA".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.ordersegcode"));
    }

    public boolean isForPayment(Map<String, String> requestData) {
        return "HKCCS".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.ordersegcode"));
    }

    public boolean isForPaymentStatus(Map<String, String> requestData) {
        return "HKIPS".equals(MapRegexUtil.getDataRegex(requestData, "GV\\.InstantUebSEPAStatus1.SegHead\\.code"));
    }
}
