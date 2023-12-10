package com.github.toastshaman.tinytypes.fp.db.jooq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.SQLDialect.H2;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JooqTransactionTest {

    JdbcDataSource dataSource;

    DSLContext context;

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

        context = DSL.using(dataSource, H2);
    }

    @Test
    void runs_query_and_delete_as_part_of_a_transaction() {
        var numberOfDeletedRecords =
                findVetById(1).flatMap(v -> deleteVetById(v.id)).apply(context);

        assertThat(numberOfDeletedRecords)
                .withFailMessage("should have deleted one record")
                .isEqualTo(1);
    }

    private JooqTransaction<Vet> findVetById(int id) {
        return JooqTransaction.of(ctx -> ctx.dsl()
                .fetchOne("SELECT * FROM vets WHERE id = ?", id)
                .map(record -> new Vet(
                        record.getValue(0, Integer.class),
                        record.getValue(1, String.class),
                        record.getValue(2, String.class))));
    }

    private JooqTransaction<Integer> deleteVetById(int id) {
        return JooqTransaction.of(cfg ->
                cfg.dsl().deleteFrom(table("vets")).where(field("id").eq(id)).execute());
    }

    record Vet(Integer id, String firstName, String lastName) {}
}
