package de.adorsys.opba.db.repository;

import de.adorsys.opba.db.domain.entity.Bank;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class BankRepositoryImpl {

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String query, int startPos, int maxResults) {
        String subQuery = "SELECT id, uuid, name, bic, bank_code, word_similarity(:keyword, field) as sml "
                + "FROM opb_bank WHERE :keyword <% field ";

        String sql = Stream.of("name", "bank_code", "bic")
                .map(s -> subQuery.replaceAll("field", s))
                .collect(Collectors.joining("UNION "))
                + "ORDER BY sml DESC "
                + "LIMIT :max "
                + "OFFSET :start";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("keyword", query);
        parameters.addValue("max", maxResults);
        parameters.addValue("start", startPos);

        return jdbcTemplate.query(sql, parameters, ROW_MAPPER);
    }
}
