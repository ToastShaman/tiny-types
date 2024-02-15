package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static java.util.Objects.requireNonNull;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.MetadataEvent;
import java.util.Optional;

public record MermaidNode(Event event) {

    public MermaidNode {
        requireNonNull(event);
    }

    public String id() {
        return (event instanceof MetadataEvent m ? m.event().getClass() : event.getClass()).getSimpleName();
    }

    public Optional<String> maybeText() {
        return event instanceof MetadataEvent meta ? meta.maybe("mermaid_node_text") : Optional.empty();
    }

    public Optional<String> maybeShape() {
        return event instanceof MetadataEvent meta ? meta.maybe("mermaid_node_shape") : Optional.empty();
    }

    public Optional<String> maybeEdgeLabel() {
        return event instanceof MetadataEvent meta ? meta.maybe("mermaid_edge_text") : Optional.empty();
    }

    public String render() {
        var maybeShape = maybeShape().orElse(null);
        var maybeText = maybeText().orElse(null);
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

        return fmt.formatted(id(), maybeText == null ? id() : maybeText);
    }

    public static MermaidNode of(Event event) {
        return new MermaidNode(event);
    }
}
