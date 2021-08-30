package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.BankSearchRepositoryImpl;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.tppbankingapi.search.model.generated.BankDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.generated.BankProfileDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankProfileJpaRepository bankProfileJpaRepository;
    private final BankSearchRepositoryImpl bankRepository;

    @Transactional(readOnly = true)
    public Optional<BankProfileDescriptor> getBankProfile(UUID profileUuid, boolean onlyActive) {
        return bankProfileJpaRepository.findByUuid(profileUuid)
                .filter(profile -> !onlyActive || profile.isActive())
                .map(BankProfile.TO_BANK_PROFILE_DESCRIPTOR::map);
    }

    @Transactional(readOnly = true)
    public List<BankDescriptor> getBanks(String query, int startPos, int maxResults, boolean onlyActive) {
        return bankRepository.getBanks(query, startPos, maxResults)
                .stream()
                .filter(bank -> !onlyActive || bank.isActive())
                .map(Bank.TO_BANK_DESCRIPTOR::map)
                .collect(Collectors.toList());
    }
}
