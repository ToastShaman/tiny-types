package com.github.toastshaman.tinytypes.events;

import static java.util.Collections.synchronizedList;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class MermaidVisualiser implements Events {

    private final List<Event> captured = synchronizedList(new ArrayList<>());

    @Override
    public void record(Event event) {
        captured.add(event);
    }

    private List<String> nodes() {
        return captured.stream().map(Event::getClass).map(Class::getSimpleName).toList();
    }

    public String asString() {
        var template = """
                flowchart TB
                { nodes }
                """;
        var nodes = String.join(" --> ", nodes());
        return template.replace("{ nodes }", nodes);
    }

    public String asHtml() {
        var writer = new StringWriter();
        asHtml(writer);
        return writer.toString();
    }

    public void asHtml(Writer writer) {
        var template =
                """
                        <!DOCTYPE html>
                        <html lang="en">
                          <body>
                            <pre class="mermaid">
                                flowchart TB
                                { nodes }
                            </pre>
                            <script type="module">
                              import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                            </script>
                          </body>
                        </html>
                        """;

        var nodes = String.join(" --> ", nodes());
        var diagram = template.replace("{ nodes }", nodes);

        try {
            writer.write(diagram);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
