package com.github.toastshaman.tinytypes.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class EventTest {

    @Test
    void can_return_a_metadata_event() {
        var event = MyEvent.random().addMetadata("key", "value");

        assertThat(event).isInstanceOfSatisfying(MetadataEvent.class, it -> assertThat(it.metadata())
                .containsEntry("key", "value"));
    }
}
