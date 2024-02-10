package com.github.toastshaman.tinytypes.events;

import static java.util.Collections.synchronizedList;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class MermaidEventRenderer implements Events {

    public enum MermaidOutputFormat {
        RAW,
        HTML,
        MARKDOWN
    }

    private static final Function<Event, String> EVENT_TO_MERMAID_NODE_WITH_TEXT = event -> {
        if (!(event instanceof MetadataEvent m)) {
            return event.getClass().getSimpleName();
        }

        var eventName = m.event().getClass().getSimpleName();
        var maybeDisplayText = (String) m.metadata().get("mermaid_node_text");
        var maybeShape = (String) m.metadata().get("mermaid_node_shape");
        var fmt =
                switch (maybeShape) {
                    case "rounded" -> "%s(%s)";
                    case "subroutine" -> "%s[[%s]]";
                    case "database" -> "%s[(%s)]";
                    case "circle" -> "%s((%s))";
                    case "circle_double" -> "%s(((%s)))";
                    case "asymmetric" -> "%s>%s]";
                    case "rhombus" -> "%s{%s}";
                    case "hexagon" -> "%s{{%s}}";
                    case "parallelogram" -> "%s[/%s/]";
                    case "parallelogram_alt" -> "%s[\\%s\\]";
                    case "trapezoid" -> "%s[/%s\\]";
                    case "trapezoid_alt" -> "%s[\\%s/]";
                    case null -> "%s[%s]";
                    default -> throw new IllegalStateException("Unexpected value: " + maybeShape);
                };

        return fmt.formatted(eventName, maybeDisplayText == null ? eventName : maybeDisplayText);
    };

    private static final Function<Event, String> EVENT_TO_MERMAID_NODE = event -> event instanceof MetadataEvent m
            ? m.event().getClass().getSimpleName()
            : event.getClass().getSimpleName();

    private final List<Event> captured = synchronizedList(new ArrayList<>());

    @Override
    public void record(Event event) {
        captured.add(event);
    }

    private List<String> nodesWithText() {
        return captured.stream().map(EVENT_TO_MERMAID_NODE_WITH_TEXT).toList();
    }

    private List<String> nodesWithLinks() {
        return captured.stream().map(EVENT_TO_MERMAID_NODE).toList();
    }

    public void render(MermaidOutputFormat outputFormat, Writer writer) {
        var template =
                switch (outputFormat) {
                    case RAW -> """
                    flowchart TB
                    { nodes_with_text }
                    { nodes_with_links }
                    """;
                    case HTML -> """
                    <!DOCTYPE html>
                    <html lang="en">
                      <body>
                        <pre class="mermaid">
                            flowchart TB
                            { nodes_with_text }
                            { nodes_with_links }
                        </pre>
                        <script type="module">
                          import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                        </script>
                      </body>
                    </html>
                    """;
                    case MARKDOWN -> """
                    ```mermaid
                    flowchart TB
                    { nodes_with_text }
                    { nodes_with_links }
                    ```
                    """;
                };

        var nodes = String.join("\n", nodesWithText());
        var links = String.join(" --> ", nodesWithLinks());
        var output = template.replace("{ nodes_with_text }", nodes).replace("{ nodes_with_links }", links);

        try {
            writer.write(output);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
