package com.github.toastshaman.tinytypes.format.jackson;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JsonTest {

    String json = """
                    {
                      "glossary": {
                        "title": "example glossary",
                        "id": 5,
                        "GlossDiv": {
                          "title": "S",
                          "GlossList": {
                            "GlossEntry": {
                              "ID": "SGML",
                              "SortAs": "SGML",
                              "GlossTerm": "Standard Generalized Markup Language",
                              "Acronym": "SGML",
                              "Abbrev": "ISO 8879:1986",
                              "GlossDef": {
                                "para": "A meta-markup language, used to create markup languages such as DocBook.",
                                "GlossSeeAlso": [
                                  "GML",
                                  "XML"
                                ]
                              },
                              "GlossSee": "markup"
                            }
                          }
                        }
                      }
                    }""";

    @Test
    void can_read_as_json_object() {
        var result = Json.standard().decoder().readJSONObject(json);

        assertThat(result).isSuccess();
    }

    @Test
    void can_read_list() {
        var result = Json.standard().decoder().readList("[1,2,3,4]");

        assertThat(result).hasValueSatisfying(it -> assertThat(it).hasSize(4));
    }

    @Test
    void can_write_json() {
        var json = Json.standard().encoder().write(Map.of("message", "hello")).getOrNull();

        assertThatJson(json).isEqualTo(Map.of("message", "hello"));
    }
}
