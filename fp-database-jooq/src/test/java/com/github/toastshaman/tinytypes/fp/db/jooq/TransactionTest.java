package com.github.toastshaman.tinytypes.fp.db.jooq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.SQLDialect.H2;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TransactionTest {

    DSLContext context;

    HikariDataSource dataSource;

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

        context = DSL.using(dataSource, H2);
    }

    @AfterEach
    void teardown() {
        dataSource.close();
    }

    @Test
    void runs_query_and_delete_as_part_of_a_transaction() {
        var numberOfDeletedRecords =
                findVetById(1).flatMap(v -> deleteVetById(v.id)).execute(context);

        assertThat(numberOfDeletedRecords)
                .withFailMessage("should have deleted one record")
                .isEqualTo(1);
    }

    Transaction<Vet> findVetById(int id) {
        return Transaction.of(ctx -> ctx.dsl()
                .fetchOne("SELECT * FROM vets WHERE id = ?", id)
                .map(record -> new Vet(
                        record.getValue(0, Integer.class),
                        record.getValue(1, String.class),
                        record.getValue(2, String.class))));
    }

    Transaction<Integer> deleteVetById(int id) {
        return Transaction.of(cfg ->
                cfg.dsl().deleteFrom(table("vets")).where(field("id").eq(id)).execute());
    }

    record Vet(Integer id, String firstName, String lastName) {}
}
