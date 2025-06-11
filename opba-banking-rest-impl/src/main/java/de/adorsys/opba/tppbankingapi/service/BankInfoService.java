package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.repository.BankInfoRepositoryImpl;
import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.BankInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankInfoService {

    private final BankInfoRepositoryImpl bankInfoRepository;

    @Transactional(readOnly = true)
    public BankInfoResponse getBankInfoByIban(String ibanStr) {
        try {
            log.info("Parsing IBAN: {}", ibanStr);
            Iban iban = Iban.valueOf(ibanStr);
            String bankCode = iban.getBankCode();
            log.info("Extracted bank code: {}", bankCode);

            Optional<Bank> bankOpt = bankInfoRepository.findByIban(bankCode, false);
            if (bankOpt.isEmpty()) {
                log.warn("No bank found for code: {}", bankCode);
                return null;
            }

            Bank bank = bankOpt.get();
            BankInfoResponse response = new BankInfoResponse();
            response.setBankCode(bank.getBankCode());
            response.setBankName(bank.getName());
            response.setBic(bank.getBic());
            return response;

        } catch (IbanFormatException | IllegalArgumentException e) {
            // Invalid IBAN
            log.error("Invalid IBAN: {}", ibanStr, e);
            return null;
        }
    }
}
