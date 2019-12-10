package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.repository.BankRepositoryImpl;
import de.adorsys.opba.core.protocol.repository.jpa.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepositoryJpa;
    private final BankRepositoryImpl bankRepository;

    public List<Bank> getBanks(String query, int maxResults) {
        return bankRepository.getBanks(query, maxResults);
    }

    public Bank getBankProfile(Long id) {
        return bankRepositoryJpa.getOne(id);
    }

    public List<Bank> getBanksFTS(String query, int maxResults) {
        return bankRepository.getBanksFTS(query, maxResults);
    }
}
