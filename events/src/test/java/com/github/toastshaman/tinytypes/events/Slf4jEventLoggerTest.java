package com.github.toastshaman.tinytypes.events;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

@DisplayNameGeneration(ReplaceUnderscores.class)
class Slf4jEventLoggerTest {

    private record MyEvent(String message) implements Event {
        private MyEvent {
            Objects.requireNonNull(message);
        }

        @SuppressWarnings("MethodNameSameAsClassName")
        public static MyEvent MyEvent(String message) {
            return new MyEvent(message);
        }
    }

    private final ObjectMapper mapper =
            JsonMapper.builder().addModule(new JsonOrgModule()).build();

    @Test
    void log() {
        BiConsumer<Marker, String> assertion =
                (m, s) -> assertThatJson(s).isEqualTo("""
                {"message":"Hello World"}""");

        Function<Event, JSONObject> objectFunction = it -> mapper.convertValue(it, JSONObject.class);

        new Slf4jEventLogger(assertion, objectFunction).record(MyEvent.MyEvent("Hello World"));
    }
}
