package de.adorsys.opba.protocol.hbci.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.hbci.service.storage.TransientDataEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
            ProtocolAction.LIST_TRANSACTIONS, "hbci-list-transactions"
    );

    @JsonIgnore
    public String getPsuPassword() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getPsuPassword() : null;
    }

    @JsonIgnore
    public String getLastScaChallenge() {
        TransientDataEntry entry = this.transientStorage().get();
        return null != entry ? entry.getScaChallengeResult() : null;
    }

    @JsonIgnore
    public void setPsuPassword(String psuPassword) {
        this.transientStorage().set(new TransientDataEntry(psuPassword, null));
    }

    @JsonIgnore
    public void setLastScaChallenge(String scaChallengeResult) {
        this.transientStorage().set(new TransientDataEntry(null, scaChallengeResult));
    }
}
