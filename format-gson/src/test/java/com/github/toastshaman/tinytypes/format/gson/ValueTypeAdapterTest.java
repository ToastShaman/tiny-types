package com.github.toastshaman.tinytypes.format.gson;

import com.github.toastshaman.tinytypes.test.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

class ValueTypeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new ValueTypeAdapterFactory())
            .create();

    @Test
    void serializes_and_deserializes() throws JSONException {
        Firstname firstname = new Firstname("Mete");
        Lastname lastname = new Lastname("Dietfried");
        Age age = new Age(42);
        Hobby hobby = new Hobby("Playing Guitar");
        Timestamp timestamp = new Timestamp(BigInteger.ONE);
        Pin pin = new Pin("1234567890");

        Person person = new Person(firstname, lastname, age, List.of(hobby), timestamp, pin);

        String json = gson.toJson(person);

        JSONAssert.assertEquals("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":42,\"hobbies\":[\"Playing Guitar\"],\"timestamp\":1,\"pin\":\"1234567890\"}", json, STRICT);

        Person personFromWire = gson.fromJson(json, Person.class);

        assertThat(personFromWire).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    void fails_validation_from_wire() {
        assertThatThrownBy(() -> gson.fromJson("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":300}", Person.class))
                .hasMessageContaining("Age: [must be less than or equal to 120]");
    }

}
