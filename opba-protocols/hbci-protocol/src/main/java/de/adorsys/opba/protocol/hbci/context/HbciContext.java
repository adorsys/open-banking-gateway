package de.adorsys.opba.protocol.hbci.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.multibanking.hbci.model.HbciTanSubmit;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import de.adorsys.opba.protocol.hbci.service.storage.TransientDataEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HbciContext extends BaseContext {

    /**
     * PSU user ID (login) in ASPSP API
     */
    private String psuId;

    /**
     * Hardcoded process names based on the action name.
     */
    private Map<ProtocolAction, String> flowByAction = ImmutableMap.of(
            ProtocolAction.LIST_ACCOUNTS, "hbci-list-accounts",
            ProtocolAction.LIST_TRANSACTIONS, "hbci-list-transactions",
            ProtocolAction.SINGLE_PAYMENT, "hbci-single-payment",
            ProtocolAction.GET_PAYMENT_STATUS, "hbci-payment-status",
            ProtocolAction.GET_PAYMENT_INFORMATION, "hbci-payment-status"

    );

    /**
     * FinTech WebHook that will be called by OpenBanking (and user will be redirected to) if the consent
     * was <b>granted</b> during authorization.
     */
    private String fintechRedirectUriOk;

    /**
     * FinTech WebHook that will be called by OpenBanking (and user will be redirected to) if the consent
     * was <b>declined</b> during authorization.
     */
    private String fintechRedirectUriNok;

    /**
     * HBCI bank profile.
     */
    private Bank bank;

    /**
     * HBCI dialog requires this entity to be persisted (i.e. for systemDialogId). It contains sensitive data like PIN/TAN,
     * user account. Protected by {@code de.adorsys.opba.protocol.bpmnshared.config.flowable.JsonCustomSerializer}
     * and {@code de.adorsys.opba.protocol.bpmnshared.config.flowable.LargeJsonCustomSerializer}
     */
    private HbciConsent hbciDialogConsent;

    /**
     * This is typed as Object in HbciConsent which causes serialize/deserialize issues as its type is not saved.
     * In order not to get dirty with Jackson class serialization (in particular additional sanitization layer),
     * storing this field separately.
     */
    private HbciTanSubmit hbciTanSubmit;

    /**
     * Indicates whether TAN challenge was required.
     */
    private boolean tanChallengeRequired;

    /**
     * Available SCA methods (i.e. SMS,email) for consent SCA challenge (2FA/multifactor authorization - 2nd factor)
     */
    private List<ScaMethod> availableSca;

    /**
     * The ID of SCA method that was selected by the user.
     */
    private String userSelectScaId;

    /**
     * Indicates that while consent exists, it is incompatible.
     */
    private boolean consentIncompatible;

    private HbciResultCache cachedResult;

    public HbciConsent getHbciDialogConsent() {
        if (null == hbciDialogConsent) {
            return null;
        }

        hbciDialogConsent.setHbciTanSubmit(hbciTanSubmit);
        return hbciDialogConsent;
    }

    public void setHbciDialogConsent(HbciConsent hbciDialogConsent) {
        this.hbciDialogConsent = hbciDialogConsent;
        this.hbciTanSubmit = null == hbciDialogConsent ? null : (HbciTanSubmit) hbciDialogConsent.getHbciTanSubmit();
    }

    @JsonIgnore
    public String getPsuPin() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getPsuPin() : null;
    }

    @JsonIgnore
    public String getPsuTan() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getTanValue() : null;
    }

    @JsonIgnore
    public void setPsuPin(String psuPassword) {
        this.transientStorage().set(new TransientDataEntry(psuPassword, null));
    }

    @JsonIgnore
    public void setPsuTan(String scaChallengeResult) {
        this.transientStorage().set(new TransientDataEntry(null, scaChallengeResult));
    }

    @JsonIgnore
    public HbciProtocolConfiguration.UrlSet getActiveUrlSet(HbciProtocolConfiguration config) {
        return ProtocolAction.SINGLE_PAYMENT.equals(this.getAction())
                ? config.getPis() : config.getAis();
    }
}
