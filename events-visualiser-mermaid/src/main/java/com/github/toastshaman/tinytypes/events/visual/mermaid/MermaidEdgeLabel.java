package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.stream.Stream;

public record MermaidEdgeLabel(MermaidNode left, MermaidNode right) {

    public MermaidEdgeLabel {
        requireNonNull(left);
        requireNonNull(right);
    }

    public static MermaidEdgeLabel from(MermaidNode left, MermaidNode right) {
        return new MermaidEdgeLabel(left, right);
    }

    public String render() {
        var labels = Stream.of(left.maybeOutgoingEdgeLabel(), right.maybeIncomingEdgeLabel())
                .flatMap(Optional::stream)
                .toList();

        if (labels.isEmpty()) {
            return "%s --> %s".formatted(left.id(), right.id());
        }

        var text = String.join("\\n", labels);
        return "%s -->|%s|%s".formatted(left.id(), text, right.id());
    }
}
