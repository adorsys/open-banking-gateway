package de.adorsys.opba.db.repository;

import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.xs2a.adapter.api.AspspReadOnlyRepository;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BankProfileRepositoryImpl implements AspspReadOnlyRepository {

    private final BankProfileJpaRepository bankProfileJpaRepository;

    @Override
    public Optional<Aspsp> findById(String uuid) {
        return bankProfileJpaRepository.findByUuid(UUID.fromString(uuid)).map(BankProfile.TO_ASPSP::map);
    }

    @Override
    public List<Aspsp> findByBic(String bankBic, String s1, int i) {
        return bankProfileJpaRepository.findByBankBic(bankBic).stream()
                .map(BankProfile.TO_ASPSP::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aspsp> findByBankCode(String bankCode, String s1, int i) {
        return bankProfileJpaRepository.findByBankBankCode(bankCode).stream()
                .map(BankProfile.TO_ASPSP::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aspsp> findByName(String bankName, String s1, int i) {
        return bankProfileJpaRepository.findByBankName(bankName).stream()
                .map(BankProfile.TO_ASPSP::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aspsp> findAll(String s, int i) {
        return bankProfileJpaRepository.findAll().stream()
                .map(BankProfile.TO_ASPSP::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aspsp> findLike(Aspsp aspsp, String s, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findByIban(String s, String s1, int i) {
        return null;
    }
}
