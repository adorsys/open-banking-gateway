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

    public boolean isForPaymentListing(Map<String, String> requestData) {
        return "HKKAZ".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.ordersegcode"));
    }
}
