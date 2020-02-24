package de.adorsys.opba.db.repository;

import de.adorsys.opba.db.domain.entity.Bank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BankRepositoryImpl {

    private final Environment env;

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\$\\{(.+?)\\})");

    private static final BeanPropertyRowMapper<Bank> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Bank.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Bank> getBanks(String query, int startPos, int maxResults) {
        String subQuery = "SELECT id, uuid, name, bic, bank_code, word_similarity(:keyword, field) as sml "
                + "FROM opb_bank WHERE word_similarity(:keyword, field) >= ${bank-search.keyword.threshold.field} ";

        log.info(env.getProperty("${bank-search.keyword.threshold.name}"));


        String sql = Stream.of("name", "bank_code", "bic")
                .map(s -> subQuery.replaceAll("field", s))
                .map(s -> {
                    Matcher matcher = PROPERTY_PATTERN.matcher(s);
                    if (matcher.find()) {
                        String propertyValue = env.getProperty(matcher.group(2));
                        if (null != propertyValue) {
                            return s.replace(matcher.group(1), propertyValue);
                        }
                    }
                    return s;
                })
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
