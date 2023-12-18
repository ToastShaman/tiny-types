package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.Tuple;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class LensTest {

    Person person = new Person("Irving", 50);

    Lens<Person, Integer> ageLens = Lens.of(it -> it.age, Person::withAge);

    Lens<Person, String> nameLens = Lens.of(it -> it.name, Person::withName);

    @Test
    void can_set_and_get() {
        assertThat(ageLens.get(person)).isEqualTo(50);
        assertThat(ageLens.set(person, 100)).isEqualTo(new Person("Irving", 100));
    }

    @Test
    void can_modify() {
        assertThat(ageLens.mod(person, age -> age + 1)).isEqualTo(new Person("Irving", 51));
    }

    @Test
    void can_map_and_returns_reader() {
        assertThat(nameLens.map(String::toUpperCase).apply(person)).isEqualTo("IRVING");
    }

    @Test
    void can_filter() {
        assertThat(nameLens.filter(it -> it.startsWith("A")).apply(person)).isEmpty();
        assertThat(nameLens.filter(it -> it.startsWith("I")).apply(person)).hasValue("Irving");
    }

    @Test
    void can_fold_and_get() {
        var t = Lens.fold(nameLens, ageLens).get(person);
        assertThat(t._1).isEqualTo("Irving");
        assertThat(t._2).isEqualTo(50);
    }

    @Test
    void can_fold_and_set() {
        var p = Lens.fold(nameLens, ageLens).set(person, Tuple.of("Foo", 60));
        assertThat(p.name).isEqualTo("Foo");
        assertThat(p.age).isEqualTo(60);
    }

    @Test
    void maybe() {
        assertThat(nameLens.maybe(new Person(null, 50))).isEmpty();
    }

    @Test
    void can_chain_using_then() {
        var aLens = Lens.of(A::b, A::withB);
        var idxLens = Lens.of(B::index, B::withIndex);
        var lens = aLens.andThen(idxLens);

        var value = new A(1, new B(2));

        assertThat(lens.get(value)).isEqualTo(2);
        assertThat(lens.set(value, 3)).isEqualTo(new A(1, new B(3)));
    }

    @Test
    void can_chain_using_compose() {
        var aLens = Lens.of(A::b, A::withB);
        var idxLens = Lens.of(B::index, B::withIndex);
        var lens = idxLens.compose(aLens);

        var value = new A(1, new B(2));

        assertThat(lens.get(value)).isEqualTo(2);
        assertThat(lens.set(value, 3)).isEqualTo(new A(1, new B(3)));
    }

    @Test
    void can_turn_into_a_reader() {
        assertThat(nameLens.asReader().apply(person)).isEqualTo("Irving");
    }

    record Person(String name, int age) {

        public Person withAge(int age) {
            return new Person(name, age);
        }

        public Person withName(String name) {
            return new Person(name, age);
        }
    }

    record A(int index, B b) {

        public A withB(B b) {
            return new A(index, b);
        }
    }

    record B(int index) {

        public B withIndex(int idx) {
            return new B(idx);
        }
    }
}
