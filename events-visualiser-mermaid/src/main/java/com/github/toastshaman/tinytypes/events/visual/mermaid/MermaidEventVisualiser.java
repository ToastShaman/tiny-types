package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidEventVisualiser.MermaidOrientation.TB;
import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidEventVisualiser.MermaidOutputFormat.RAW;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.synchronizedList;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.events.MetadataEvent;
import io.vavr.Function2;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import org.json.JSONObject;

public final class MermaidEventVisualiser implements Events {

    public enum MermaidOutputFormat {
        RAW,
        HTML,
        MARKDOWN
    }

    public enum MermaidOrientation {
        TB,
        TD,
        BT,
        RL,
        LR
    }

    private static final Function<Event, String> EVENT_TO_ID = event -> {
        if (event instanceof MetadataEvent m) {
            return m.event().getClass().getSimpleName();
        }
        return event.getClass().getSimpleName();
    };

    private static final Function<Event, String> EVENT_TO_MERMAID_NODE_WITH_TEXT = event -> {
        var eventName = EVENT_TO_ID.apply(event);

        if (!(event instanceof MetadataEvent meta)) {
            return eventName;
        }

        var maybeText = (String) meta.metadata().get("mermaid_node_text");
        var maybeShape = (String) meta.metadata().get("mermaid_node_shape");
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
                    case "rectangle" -> "%s[%s]";
                    case null -> "%s[%s]";
                    default -> throw new IllegalStateException("Unexpected value: %s".formatted(maybeShape));
                };

        return fmt.formatted(eventName, maybeText == null ? eventName : maybeText);
    };

    private static final Function2<Event, Event, String> EVENTS_TO_EDGE = (first, second) -> {
        String firstId = EVENT_TO_ID.apply(first);
        String secondId = EVENT_TO_ID.apply(second);
        String outgoingLabel = "";
        String incomingLabel = "";

        if (first instanceof MetadataEvent m1) {
            outgoingLabel = (String) m1.metadata().getOrDefault("mermaid_outgoing_edge_label", "");
        }

        if (second instanceof MetadataEvent m2) {
            incomingLabel = (String) m2.metadata().getOrDefault("mermaid_incoming_edge_label", "");
        }

        String label = String.join("\n", outgoingLabel, incomingLabel);

        StringBuilder fmt = new StringBuilder();
        fmt.append(firstId);
        fmt.append(" ");
        fmt.append("-->");
        fmt.append(" ");
        if (!label.isBlank()) {
            fmt.append("|").append(label).append("|");
        }
        fmt.append(secondId);
        return fmt.toString();
    };

    private final List<Event> captured = synchronizedList(new ArrayList<>());

    @Override
    public void record(Event event) {
        captured.add(event);
    }

    private List<String> nodesWithText() {
        return captured.stream().map(EVENT_TO_MERMAID_NODE_WITH_TEXT).toList();
    }

    private List<String> nodesWithLinks() {
        var edges = new ArrayList<String>();

        var iterator = captured.listIterator();
        while (iterator.hasNext()) {
            var left = iterator.next();
            var right = iterator.hasNext() ? iterator.next() : null;
            if (right == null) {
                iterator.previous();
                edges.add(EVENTS_TO_EDGE.apply(iterator.previous(), left));
                break;
            } else {
                edges.add(EVENTS_TO_EDGE.apply(left, right));
            }
        }

        return edges;
    }

    public URI liveEditor() {
        return liveEditor(TB);
    }

    public URI liveEditor(MermaidOrientation orientation) {
        try {
            var code = renderToString(RAW, orientation);
            var options = new JSONObject().put("code", code).put("mermaid", new JSONObject().put("theme", "dark"));
            var encoded =
                    Base64.getUrlEncoder().encodeToString(options.toString().getBytes(UTF_8));
            return new URI("https://mermaid.live/edit#base64:%s".formatted(encoded));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String renderToString(MermaidOutputFormat outputFormat) {
        return renderToString(outputFormat, TB);
    }

    public String renderToString(MermaidOutputFormat outputFormat, MermaidOrientation orientation) {
        var writer = new StringWriter();
        render(outputFormat, orientation, writer);
        return writer.toString();
    }

    public void render(MermaidOutputFormat outputFormat, Writer writer) {
        render(outputFormat, TB, writer);
    }

    public void render(MermaidOutputFormat outputFormat, MermaidOrientation orientation, Writer writer) {
        var template =
                switch (outputFormat) {
                    case RAW -> """
                            flowchart { orientation }
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
                            flowchart { orientation }
                            { nodes_with_text }
                            { nodes_with_links }
                            ```
                            """;
                };

        var nodes = String.join("\n", nodesWithText());
        var links = String.join("\n", nodesWithLinks());
        var output = template.replace("{ orientation }", orientation.name())
                .replace("{ nodes_with_text }", nodes)
                .replace("{ nodes_with_links }", links);

        try {
            writer.write(output);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
