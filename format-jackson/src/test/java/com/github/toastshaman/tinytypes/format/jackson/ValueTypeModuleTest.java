package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.toastshaman.tinytypes.test.*;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

class ValueTypeModuleTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(new ValueTypeModule());

    @Test
    void serializes_and_deserializes() throws IOException, JSONException {
        Firstname firstname = new Firstname("Mete");
        Lastname lastname = new Lastname("Dietfried");
        Age age = new Age(42);
        Hobby hobby = new Hobby("Playing Guitar");
        Timestamp timestamp = new Timestamp(BigInteger.ONE);
        Pin pin = new Pin("1234567890");

        Person person = new Person(firstname, lastname, age, List.of(hobby), timestamp, pin);
        String json = mapper.writer().writeValueAsString(person);

        JSONAssert.assertEquals("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":42,\"hobbies\":[\"Playing Guitar\"],\"timestamp\":1,\"pin\":\"1234567890\"}", json, STRICT);

        Person personFromWire = mapper.reader().readValue(json, Person.class);

        assertThat(personFromWire).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    void fails_validation_from_wire() {
        assertThatThrownBy(() -> mapper.reader().readValue("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":300}", Person.class))
                .hasMessageContaining("Age: [must be less than or equal to 120]");
    }
}