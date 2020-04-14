package de.adorsys.opba.fintech.server.feignmocks;

import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TppBankSearchClientFeignMock implements TppBankSearchClient {

    @Override
    public ResponseEntity<BankProfileResponse> bankProfileGET(UUID xRequestID,
                                                              @NotNull @Valid String bankId,
                                                              String xTimestampUTC,
                                                              String xRequestSignature,
                                                              String fintechId) {
        return null;
    }

    @Override
    public ResponseEntity<BankSearchResponse> bankSearchGET(
            UUID xRequestID,
            @NotNull @Valid String keyword,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            @Valid Integer start,
            @Valid Integer max) {

        return null;
    }
}
