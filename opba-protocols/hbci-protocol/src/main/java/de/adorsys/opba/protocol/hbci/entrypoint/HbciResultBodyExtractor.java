package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Extracts HBCI result from ASPSP response and does initial translation to Banking protocol facade native object
 * for transactions or accounts list.
 */
@Service
@RequiredArgsConstructor
public class HbciResultBodyExtractor {

    public AccountListBody extractAccountList(ProcessResponse result) {
        return AccountListBody.builder()
                .build();
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result) {
        return null;
    }
}
