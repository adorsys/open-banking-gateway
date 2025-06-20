package de.adorsys.opba.db.repository;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BankInfoRepositoryImpl {

    @Value("${" + BANKING_API_CONFIG_PREFIX + "bank-info.query}")
    private String query;

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BankProfileJpaRepository profileRepository;

    public Optional<Bank> findByIban(String bankCode, boolean onlyActive) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("bankCode", bankCode);
        parameters.addValue("onlyActive", onlyActive);

        List<Bank> results = jdbcTemplate.query(query, parameters, ROW_MAPPER);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Bank bank = results.get(0);
        if (onlyActive) {
            List<BankProfile> activeProfiles = profileRepository.findByBankId(bank.getId())
                    .stream()
                    .filter(BankProfile::isActive)
                    .toList();
            bank.setProfiles(activeProfiles);
        } else {
            List<BankProfile> allProfiles = profileRepository.findByBankId(bank.getId());
            bank.setProfiles(allProfiles);
        }

        return Optional.of(bank);
    }
}
