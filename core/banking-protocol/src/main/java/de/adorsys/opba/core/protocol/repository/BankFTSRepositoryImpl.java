package de.adorsys.opba.core.protocol.repository;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BankFTSRepositoryImpl {

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String query, int maxResults) {

        String sql = "SELECT id, name, bic, bank_code, word_similarity(?, (name || ' ' || bic || ' ' || bank_code)) as sim " +
                "FROM opb_bank order by sim desc limit ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, query, maxResults);
    }
}
