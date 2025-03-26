package de.adorsys.opba.fintech.server.feignmocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public class TppBankSearchClientFeignMock implements TppBankSearchClient {

    @Override
    public ResponseEntity<BankProfileResponse> bankProfileGET(UUID xRequestID,
                                                              @NotNull @Valid UUID bankProfileID,
                                                              String xTimestampUTC,
                                                              String xRequestSignature,
                                                              String fintechId,
                                                              Boolean onlyActive) {
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
            @Valid Integer max,
            Boolean onlyActive) {

        return null;
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }
}
