package com.github.toastshaman.tinytypes.fp.db.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SpringJdbcActionTest {

    HikariDataSource dataSource;

    NamedParameterJdbcTemplate jdbcTemplate;

    TransactionTemplate txTemplate;

    @BeforeEach
    void setup() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:db");
        config.setUsername("sa");
        config.setPassword("sa");

        dataSource = new HikariDataSource(config);

        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        jdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));

        txTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));
    }

    @AfterEach
    void teardown() {
        dataSource.close();
    }

    @Test
    void runs_query_and_delete_as_part_of_a_transaction() {
        var numberOfDeletedRecords = findVetById(1)
                .flatMap(it -> deleteVetById(it.id))
                .withTransaction(txTemplate)
                .execute(jdbcTemplate);

        assertThat(numberOfDeletedRecords)
                .withFailMessage("should have deleted one record")
                .isEqualTo(1);
    }

    SpringJdbcAction<Vet> findVetById(int id) {
        return SpringJdbcAction.of(
                t -> t.queryForObject("SELECT * FROM vets WHERE id = :id", Map.of("id", id), (rs, rowNum) -> {
                    var vetId = rs.getInt(1);
                    var firstName = rs.getString(2);
                    var lastName = rs.getString(3);
                    return new Vet(vetId, firstName, lastName);
                }));
    }

    SpringJdbcAction<Integer> deleteVetById(int id) {
        return SpringJdbcAction.of(t -> t.update("DELETE FROM vets WHERE id = :id", Map.of("id", id)));
    }

    record Vet(Integer id, String firstName, String lastName) {}
}
