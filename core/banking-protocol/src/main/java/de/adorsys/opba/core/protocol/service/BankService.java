package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.repository.BankRepositoryImpl;
import de.adorsys.opba.core.protocol.repository.jpa.BankProfileRepository;
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
