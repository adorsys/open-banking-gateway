package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.repository.BankFTSRepositoryImpl;
import de.adorsys.opba.core.protocol.repository.jpa.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankFTSRepositoryImpl bankFTSRepository;

    public List<Bank> getBanks(String query, int maxResults) {
        return bankRepository.findByNameContainingIgnoreCaseOrBicContainingIgnoreCaseOrBankCodeContainingIgnoreCase(
                query, query, query, PageRequest.of(0, maxResults));
    }

    public Bank getBankProfile(Long id) {
        return bankRepository.getOne(id);
    }

    public List<Bank> getBanksFTS(String query, int maxResults) {
        return bankFTSRepository.getBanks(query, maxResults);
    }
}
