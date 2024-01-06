package com.github.toastshaman.tinytypes.format.jackson;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObjectMappersTest {

    String json =
            """
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
                    }
                    """;

    @Test
    void parse_returns_success() {
        var title =
                ObjectMappers.parse(json).readString("glossary.GlossDiv.title").getOrNull();

        assertThat(title).isEqualTo("S");

        var id = ObjectMappers.parse(json).read("glossary.id", Long.class).getOrNull();

        assertThat(id).isEqualTo(5L);
    }

    @Test
    void parse_returns_failure() {
        var result = ObjectMappers.parse(json).readString("glossary.foobar.title");

        assertThat(result).isFailure();
    }

    @Test
    void can_read_as_json_object() {
        var result = ObjectMappers.readAsJSONObject(json);

        assertThat(result).isSuccess();
    }
}
