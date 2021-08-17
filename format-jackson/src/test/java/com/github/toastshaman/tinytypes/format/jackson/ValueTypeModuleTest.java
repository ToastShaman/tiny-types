package com.github.toastshaman.tinytypes.format.jackson;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.toastshaman.tinytypes.values.LongValue;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;
import com.github.toastshaman.tinytypes.values.UUIDValue;
import java.io.IOException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ValueTypeModuleTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ValueTypeModule()
                    .text(Firstname.class, Firstname::new)
                    .text(Lastname.class, Lastname::new)
                    .uuid(MyVersion.class, MyVersion::new)
                    .number(MyNumber.class, MyNumber::new));

    @Test
    void serializes_and_deserializes() throws IOException {
        var firstname = new Firstname("Mete");
        var lastname = new Lastname("Dietfried");
        var person = new Person(firstname, lastname);
        var json = mapper.writer().writeValueAsString(person);

        assertThatJson(json).isEqualTo("{firstname: 'Mete', lastname: 'Dietfried'}");

        var personFromWire = mapper.reader().readValue(json, Person.class);

        Assertions.assertThat(personFromWire).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    void fails_validation_from_wire() {
        Assertions.assertThatThrownBy(() ->
                        mapper.readValue("{\"firstname\":\"  \",\"lastname\":\"Dietfried\",\"age\":300}", Person.class))
                .hasMessageContaining("Firstname: [must not be blank]");
    }

    @Test
    void serializes_uuid_values() throws JsonProcessingException {
        var uuidValue = mapper.readValue(
                "{\"version\": \"b30c1ae7-1154-4778-8cae-ed0ab5aba977\", \"number\": 99}", MyEvent.class);

        Assertions.assertThat(uuidValue.version)
                .isEqualTo(new MyVersion(UUID.fromString("b30c1ae7-1154-4778-8cae-ed0ab5aba977")));

        Assertions.assertThat(uuidValue.number).isEqualTo(new MyNumber(99L));
    }

    private record Person(Firstname firstname, Lastname lastname) {}

    private static class Firstname extends NonBlankStringValue {

        public Firstname(String value) {
            super(value, AlwaysValid());
        }
    }

    private static class Lastname extends NonBlankStringValue {

        public Lastname(String value) {
            super(value, AlwaysValid());
        }
    }

    private static class MyVersion extends UUIDValue {

        public MyVersion(UUID value) {
            super(value);
        }
    }

    private static class MyNumber extends LongValue {

        public MyNumber(Long value) {
            super(value, AlwaysValid());
        }
    }

    private record MyEvent(MyVersion version, MyNumber number) {}
}
