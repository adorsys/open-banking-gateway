package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;

@JGivenStage
public class HbciAccountInformationRequest<SELF extends HbciAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    public SELF fintech_calls_list_accounts_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_accounts_for_max_musterman(BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_transactions_for_max_musterman(BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID);
    }
}
