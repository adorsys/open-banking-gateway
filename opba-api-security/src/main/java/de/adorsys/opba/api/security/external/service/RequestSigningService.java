package de.adorsys.opba.api.security.external.service;

import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentStatusDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;

public interface RequestSigningService {

    /**
     * Signs data for '/v1/banking/ais/accounts' opba endpoint
     *
     * @param aisListAccountsDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(AisListAccountsDataToSign aisListAccountsDataToSign);

    /**
     * Signs data for '/v1/banking/ais/accounts/{account-id}/transactions' opba endpoint
     *
     * @param aisListTransactionsDataToSign Header and query data, required for signing
     * @return String signature representation
     */
    String signature(AisListTransactionsDataToSign aisListTransactionsDataToSign);

    /**
     * Signs data for '/v1/banking/search/bank-search' opba endpoint
     *
     * @param bankSearchDataToSign Header and query data, required for signing
     * @return String signature representation
     */
    String signature(BankSearchDataToSign bankSearchDataToSign);

    /**
     * Signs data for '/v1/banking/search/bank-profile' opba endpoint
     *
     * @param bankProfileDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(BankProfileDataToSign bankProfileDataToSign);

    /**
     * Signs data for '/v1/banking/consents/{auth-id}/confirm' opba endpoint
     *
     * @param confirmConsentDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(ConfirmConsentDataToSign confirmConsentDataToSign);

    /**
     * Signs data for POST '/v1/banking/pis/payments/{payment-product}' opba endpoint
     *
     * @param paymentInitiationDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(PaymentInitiationDataToSign paymentInitiationDataToSign);

    /**
     * Signs data for GET '/v1/banking/pis/payments/{payment-product}' opba endpoint
     *
     * @param paymentInitiationDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(GetPaymentDataToSign paymentInitiationDataToSign);

    /**
     * Signs data for GET '/v1/banking/pis/payments/{payment-product}/status' opba endpoint
     *
     * @param paymentInitiationDataToSign Header data, required for signing
     * @return String signature representation
     */
    String signature(GetPaymentStatusDataToSign paymentInitiationDataToSign);
}
