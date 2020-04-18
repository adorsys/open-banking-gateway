package de.adorsys.opba.db.repository;

import de.adorsys.opba.db.domain.entity.Bank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BankSearchRepositoryImpl {

    @Value("${bank-search.query}")
    private String query;

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String keyword, int startPos, int maxResults) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("keyword", keyword);
        parameters.addValue("max", maxResults);
        parameters.addValue("start", startPos);

        return jdbcTemplate.query(query, parameters, ROW_MAPPER);
    }
}
