package de.adorsys.opba.tppbanking.impl.service;

import de.adorsys.opba.tppbanking.impl.domain.entity.Bank;
import de.adorsys.opba.tppbanking.impl.domain.entity.BankProfile;
import de.adorsys.opba.tppbanking.impl.repository.jpa.BankProfileRepository;
import de.adorsys.opba.tppbanking.impl.repository.BankRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankProfileRepository bankProfileRepository;
    private final BankRepositoryImpl bankRepository;

    public Optional<BankProfile> getBankProfile(String bankId) {
        return bankProfileRepository.findByBankUuid(bankId);
    }

    public List<Bank> getBanks(String query, int startPos, int maxResults) {
        return bankRepository.getBanks(query, startPos, maxResults);
    }
}
