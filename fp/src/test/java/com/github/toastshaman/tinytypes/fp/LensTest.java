package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class LensTest {

    @Test
    void can_set_and_get() {
        record Person(int age) {
            public Person withAge(int age) {
                return new Person(age);
            }
        }

        var ageLens = Lens.of(it -> it.age, Person::withAge);

        var person = new Person(50);
        assertThat(ageLens.get(person)).isEqualTo(50);
        assertThat(ageLens.set(person, 100)).isEqualTo(new Person(100));
    }
}
