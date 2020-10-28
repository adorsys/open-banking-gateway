package de.adorsys.opba.fireflyexporter.dto;

import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class AnalyzeableTransaction extends TransactionDetails {

    private static final String LANG_EN = "EN";
    private static final String LANG_DE = "DE";

    @Delegate
    private final TransactionDetails details;

    @Getter
    @Setter
    private String category;

    @Getter
    @Setter
    private String subCategory;

    @Getter
    @Setter
    private String specification;

    public String getIsoLanguage() {
        if (null == getCreditorAccount() || null == getDebtorAccount()) {
            return LANG_EN;
        }

        if (StringUtils.startsWithIgnoreCase(getCreditorAccount().getIban(), LANG_DE)
                && StringUtils.startsWithIgnoreCase(getDebtorAccount().getIban(), LANG_DE)) {
            return LANG_DE;
        }

        return LANG_EN;
    }

    public String getReferenceName() {
        if (BigDecimal.ZERO.compareTo(new BigDecimal(getTransactionAmount().getAmount())) > 0) {
            return normalize(getCreditorName());
        }

        return normalize(getDebtorName());
    }

    public String getPurpose() {
        if (StringUtils.hasLength(getRemittanceInformationStructured())) {
            return normalize(getRemittanceInformationStructured());
        }

        if (StringUtils.hasLength(getRemittanceInformationUnstructured())) {
            return normalize(getRemittanceInformationUnstructured());
        }

        return null;
    }

    private static String normalize(String input) {
        if (null == input) {
            return null;
        }

        return input
                .replace("Ä", "Ae")
                .replace("ä", "ae")
                .replace("Ü", "Ue")
                .replace("ü", "ue")
                .replace("Ö", "Oe")
                .replace("ö", "oe")
                .replace("ß", "ss")
                .toUpperCase();
    }
}