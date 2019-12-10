package de.adorsys.opba.core.protocol.repository;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
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

    public List<Bank> getBanks(String query, int maxResults) {
        String sql = "SELECT id, name, bic, bank_code "
                + "FROM opb_bank "
                + "WHERE name ILIKE ? OR bic ILIKE ? OR bank_code ILIKE ? "
                + "LIMIT ?";
        String queryPercent = "%" + query + "%";
        return jdbcTemplate.query(sql, ROW_MAPPER, queryPercent, queryPercent, queryPercent, maxResults);
    }

    public List<Bank> getBanksFTS(String query, int maxResults) {
        String sql = "SELECT id, name, bic, bank_code, "
                + "word_similarity(?, (name || ' ' || bic || ' ' || bank_code)) as sim "
                + "FROM opb_bank "
                + "WHERE ? <% (name || ' ' || bic || ' ' || bank_code)"
                + "ORDER BY sim DESC "
                + "LIMIT ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, query, query, maxResults);
    }
}
