package com.github.toastshaman.tinytypes.fp.db.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
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

    JdbcDataSource dataSource;

    NamedParameterJdbcTemplate jdbcTemplate;

    TransactionTemplate txTemplate;

    @BeforeEach
    public void setup() {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        jdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));

        txTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));
    }

    @Test
    void runs_query_and_delete_as_part_of_a_transaction() {
        var numberOfDeletedRecords = findVetById(1)
                .flatMap(it -> deleteVetById(it.id))
                .withTransaction(txTemplate)
                .apply(jdbcTemplate);

        assertThat(numberOfDeletedRecords)
                .withFailMessage("should have deleted one record")
                .isEqualTo(1);
    }

    private SpringJdbcAction<Vet> findVetById(int id) {
        return SpringJdbcAction.of(
                t -> t.queryForObject("SELECT * FROM vets WHERE id = :id", Map.of("id", id), (rs, rowNum) -> {
                    var vetId = rs.getInt(1);
                    var firstName = rs.getString(2);
                    var lastName = rs.getString(3);
                    return new Vet(vetId, firstName, lastName);
                }));
    }

    private SpringJdbcAction<Integer> deleteVetById(int id) {
        return SpringJdbcAction.of(t -> t.update("DELETE FROM vets WHERE id = :id", Map.of("id", id)));
    }

    record Vet(Integer id, String firstName, String lastName) {}
}
