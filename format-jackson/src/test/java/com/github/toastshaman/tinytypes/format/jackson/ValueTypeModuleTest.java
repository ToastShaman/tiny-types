package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.IntValue;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

class ValueTypeModuleTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(ValueTypeModule.value(Firstname.class))
            .registerModule(ValueTypeModule.value(Lastname.class))
            .registerModule(ValueTypeModule.value(Age.class));

    @Test
    void serializes_and_deserializes() throws IOException, JSONException {
        Firstname firstname = new Firstname("Mete");
        Lastname lastname = new Lastname("Dietfried");
        Age age = new Age(42);
        Person person = new Person(firstname, lastname, age);
        String json = mapper.writer().writeValueAsString(person);

        JSONAssert.assertEquals("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":42}", json, STRICT);

        Person personFromWire = mapper.reader().readValue(json, Person.class);

        assertThat(personFromWire).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    void fails_validation_from_wire() {
        assertThatThrownBy(() -> mapper.reader().readValue("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":300}", Person.class))
                .hasMessageContaining("Age: [300 must be smaller than 120]");
    }

    static class Person {
        public final Firstname firstname;
        public final Lastname lastname;
        public final Age age;

        Person(Firstname firstname, Lastname lastname, Age age) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.age = age;
        }
    }

    static class Firstname extends NonBlankStringValue {
        public Firstname(String value) {
            super(value, Validator.MaxLength(60));
        }
    }

    static class Lastname extends NonBlankStringValue {
        public Lastname(String value) {
            super(value, Validator.MaxLength(60));
        }
    }

    static class Age extends IntValue {
        public Age(Integer value) {
            super(value, Validator.Min(1).and(Validator.Max(120)));
        }
    }
}