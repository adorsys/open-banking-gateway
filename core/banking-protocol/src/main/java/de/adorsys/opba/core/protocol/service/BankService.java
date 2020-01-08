package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.repository.BankRepositoryImpl;
import de.adorsys.opba.core.protocol.repository.jpa.BankProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankProfileRepository bankProfileRepository;
    private final BankRepositoryImpl bankRepository;

    public List<Bank> getBanks(String query, int maxResults) {
        return bankRepository.getBanks(query, maxResults);
    }

    public BankProfile getBankProfile(Long bankId) {
        return bankProfileRepository.getOne(bankId);
    }

    public List<Bank> getBanksFTS(String query, int maxResults) {
        return bankRepository.getBanksFTS(query, maxResults);
    }
}
