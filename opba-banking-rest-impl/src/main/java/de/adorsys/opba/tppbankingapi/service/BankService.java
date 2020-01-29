package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.BankRepositoryImpl;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankProfileJpaRepository bankProfileJpaRepository;
    private final BankRepositoryImpl bankRepository;

    public Optional<BankProfile> getBankProfile(String bankId) {
        return bankProfileJpaRepository.findByBankUuid(bankId);
    }

    public List<Bank> getBanks(String query, int startPos, int maxResults) {
        return bankRepository.getBanks(query, startPos, maxResults);
    }
}
