package com.github.toastshaman.tinytypes.events.test.faker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class FakerTest {

    @Test
    void random_uuid() {
        var uuid = new Faker().uuid().random();

        assertThat(uuid.toString()).isEqualTo("062b5b81-2040-3b91-a6da-cf5e71dc8ea1");
    }

    @Test
    void random_ulid() {
        var ulid = new Faker().ulid().random();

        assertThat(ulid.toString()).isEqualTo("065DDR28207E8TDPPFBSRXS3N1");
    }
}
