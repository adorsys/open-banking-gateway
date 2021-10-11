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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BankSearchRepositoryImpl {

    @Value("${" + BANKING_API_CONFIG_PREFIX +  "bank-search.query}")
    private String query;

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final BankProfileJpaRepository profilesRepo;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String keyword, int startPos, int maxResults, boolean onlyActive) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("keyword", keyword);
        parameters.addValue("max", maxResults);
        parameters.addValue("start", startPos);
        parameters.addValue("onlyActive", onlyActive);

        var banks = jdbcTemplate.query(query, parameters, ROW_MAPPER);
        var profilesByBankId = profilesRepo.findByBankIdIn(banks.stream().map(Bank::getId).collect(Collectors.toSet())).stream()
                .filter(profile -> !onlyActive || profile.isActive())
                .collect(
                        HashMap<Long, List<BankProfile>>::new,
                        (map, v) -> map.computeIfAbsent(v.getBank().getId(), id -> new ArrayList<>()).add(v),
                        HashMap::putAll
                );
        banks.forEach(it -> {
            var profile = profilesByBankId.get(it.getId());
            if (null == it.getProfiles()) {
                it.setProfiles(new ArrayList<>());
            }
            if (null != profile) {
                it.getProfiles().addAll(profile);
            }
        });
        return banks;
    }
}
