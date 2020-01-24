package de.adorsys.opba.config.migration.hibernate;

import com.google.common.base.CaseFormat;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Converts Hibernate snakeCase naming into Database-friendly camel_case, also adds prefix to the tables.
 */
@Configuration
public class PrefixAndSnakeCasePhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final String tablePrefix;

    public PrefixAndSnakeCasePhysicalNamingStrategy(
            @Value("${spring.liquibase.parameters.table-prefix}") String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        Identifier newIdentifier = new Identifier(
                tablePrefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name.getText()),
                name.isQuoted()
        );
        return super.toPhysicalTableName(newIdentifier, context);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        Identifier newIdentifier = toSnakeCase(name);
        return super.toPhysicalSequenceName(newIdentifier, context);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        Identifier newIdentifier = toSnakeCase(name);
        return super.toPhysicalColumnName(newIdentifier, context);
    }

    @NotNull
    private Identifier toSnakeCase(Identifier name) {
        return new Identifier(
                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name.getText()),
                name.isQuoted()
        );
    }
}

