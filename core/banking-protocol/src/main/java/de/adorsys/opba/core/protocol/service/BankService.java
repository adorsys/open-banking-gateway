package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.repository.jpa.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public List<Bank> getBanks(String query, int maxResults) {
        return bankRepository.findByNameLikeAndBicLikeAndBankCodeLike(query, query, query);
    }

    public Bank getBankProfile(Long id) {
        return bankRepository.getOne(id);
    }
}
