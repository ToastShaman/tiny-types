package com.github.toastshaman.tinytypes.format.moshi;

import com.github.toastshaman.tinytypes.test.*;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

class ValueTypeAdapterTest {

    private final Moshi moshi = new Moshi.Builder()
            .add(new BigIntegerAdapter())
            .add(new ValueTypeAdapterFactory())
            .build();

    private static class BigIntegerAdapter {
        @FromJson
        public BigInteger fromJson(String value) {
            return new BigInteger(value);
        }

        @ToJson
        String toJson(BigInteger value) {
            return value.toString();
        }
    }

    @Test
    void serializes_and_deserializes() throws JSONException, IOException {
        Firstname firstname = new Firstname("Mete");
        Lastname lastname = new Lastname("Dietfried");
        Age age = new Age(42);
        Hobby hobby = new Hobby("Playing Guitar");
        Timestamp timestamp = new Timestamp(BigInteger.ONE);
        Pin pin = new Pin("1234567890");

        Person person = new Person(firstname, lastname, age, List.of(hobby), timestamp, pin);
        String json = moshi.adapter(Person.class).toJson(person);

        JSONAssert.assertEquals("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":42,\"hobbies\":[\"Playing Guitar\"],\"timestamp\":\"1\",\"pin\":\"1234567890\"}", json, STRICT);

        Person personFromWire = moshi.adapter(Person.class).fromJson(json);

        assertThat(personFromWire).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    void fails_validation_from_wire() {
        assertThatThrownBy(() -> moshi.adapter(Person.class).fromJson("{\"firstname\":\"Mete\",\"lastname\":\"Dietfried\",\"age\":300}"))
                .hasMessageContaining("Age: [must be less than or equal to 120]");
    }
}
