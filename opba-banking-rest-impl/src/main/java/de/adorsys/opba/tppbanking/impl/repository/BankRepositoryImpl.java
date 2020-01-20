package de.adorsys.opba.tppbanking.impl.repository;

import de.adorsys.opba.tppbanking.impl.domain.entity.Bank;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BankRepositoryImpl {

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final JdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String query, int startPos, int maxResults) {
        String sql = "SELECT id, uuid, name, bic, bank_code, "
                + "word_similarity(?, (name || ' ' || bic || ' ' || bank_code)) as sim "
                + "FROM opb_bank "
                + "WHERE ? <% (name || ' ' || bic || ' ' || bank_code)"
                + "ORDER BY sim DESC "
                + "LIMIT ? "
                + "OFFSET ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, query, query, maxResults, startPos);
    }
}
