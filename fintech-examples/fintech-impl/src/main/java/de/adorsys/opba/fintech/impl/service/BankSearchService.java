package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2001;
import de.adorsys.opba.fintech.api.model.generated.InlineResponse2002;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankSearchService {

    private final TppBankSearchClient tppBankSearchClient;
    private final RestRequestContext restRequestContext;

    @SneakyThrows
    public InlineResponse2001 searchBank(String keyword, Integer start, Integer max) {
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());

        BankSearchResponse bankSearchResponse = tppBankSearchClient.bankSearchGET(
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                keyword,
                start,
                max,
                true).getBody();

        InlineResponse2001 inlineResponse2001 = new InlineResponse2001().bankDescriptor(Collections.emptyList());
        if (bankSearchResponse.getBankDescriptor() != null) {
            inlineResponse2001.bankDescriptor(bankSearchResponse.getBankDescriptor().stream().map(
                    bankDescriptor -> ManualMapper.fromTppToFintech(bankDescriptor)).collect(Collectors.toList()));
        }
        inlineResponse2001.setKeyword(bankSearchResponse.getKeyword());
        inlineResponse2001.setMax(bankSearchResponse.getMax());
        inlineResponse2001.setStart(bankSearchResponse.getStart());
        inlineResponse2001.setTotal(bankSearchResponse.getTotal());
        return inlineResponse2001;
    }

    @SneakyThrows
    public InlineResponse2002 searchBankProfile(String bankId) {
        return new InlineResponse2002().bankProfile(ManualMapper.fromTppToFintech(getBankProfileById(bankId).getBody().getBankProfileDescriptor()));
    }

    public ResponseEntity<BankProfileResponse> getBankProfileById(String bankProfileId) {
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());
        return tppBankSearchClient.bankProfileGET(
                xRequestId,
                UUID.fromString(bankProfileId),
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                true);
    }
}
