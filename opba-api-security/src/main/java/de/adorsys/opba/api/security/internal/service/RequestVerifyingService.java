package de.adorsys.opba.api.security.internal.service;

import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentStatusDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;

public interface RequestVerifyingService {

    /**
     * Verifies signature data for '/v1/banking/ais/accounts' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param aisListAccountsDataToSign Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, AisListAccountsDataToSign aisListAccountsDataToSign);

    /**
     * Verifies signature data for '/v1/banking/ais/accounts/{account-id}/transactions' opba endpoint
     *
     * @param signature                     Encoded string to be verified
     * @param encodedPublicKey              Public key, used for the verification
     * @param aisListTransactionsDataToSign Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, AisListTransactionsDataToSign aisListTransactionsDataToSign);

    /**
     * Verifies signature data for '/v1/banking/search/bank-search' opba endpoint
     *
     * @param signature             Encoded string to be verified
     * @param encodedPublicKey      Public key, used for the verification
     * @param bankSearchDataToSign  Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, BankSearchDataToSign bankSearchDataToSign);

    /**
     * Verifies signature data for '/v1/banking/search/bank-profile' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param bankProfileDataToSign     Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, BankProfileDataToSign bankProfileDataToSign);

    /**
     * Verifies signature data for '/v1/banking/consents/{auth-id}/confirm' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param confirmConsentDataToSign  Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, ConfirmConsentDataToSign confirmConsentDataToSign);

    /**
     * Verifies signature data for POST '/v1/banking/pis/payments/{payment-product}' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param paymentInitiationDataToSign  Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, PaymentInitiationDataToSign paymentInitiationDataToSign);

    /**
     * Verifies signature data for GET '/v1/banking/pis/payments/{payment-product}' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param paymentInitiationDataToSign  Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, GetPaymentDataToSign paymentInitiationDataToSign);

    /**
     * Verifies signature data for GET '/v1/banking/pis/payments/{payment-product}/status' opba endpoint
     *
     * @param signature                 Encoded string to be verified
     * @param encodedPublicKey          Public key, used for the verification
     * @param paymentInitiationDataToSign  Request given data, to verify with given signature
     * @return 'true' if the signature is valid, 'false' otherwise
     */
    boolean verify(String signature, String encodedPublicKey, GetPaymentStatusDataToSign paymentInitiationDataToSign);
}
