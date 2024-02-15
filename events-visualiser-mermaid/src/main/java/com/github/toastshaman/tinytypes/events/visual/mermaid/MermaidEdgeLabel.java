package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record MermaidEdgeLabel(List<String> labels) {

    public MermaidEdgeLabel {
        requireNonNull(labels);
    }

    public static MermaidEdgeLabel from(MermaidNode left, MermaidNode right) {
        var labels = Stream.of(left.maybeOutgoingEdgeLabel(), right.maybeIncomingEdgeLabel())
                .flatMap(Optional::stream)
                .toList();

        return new MermaidEdgeLabel(labels);
    }

    public static MermaidEdgeLabel empty() {
        return new MermaidEdgeLabel(new ArrayList<>());
    }

    public String render(MermaidNode left, MermaidNode right) {
        if (labels.isEmpty()) {
            return "%s --> %s".formatted(left.id(), right.id());
        }

        var text = String.join("\\n", labels);
        return "%s -->|%s|%s".formatted(left.id(), text, right.id());
    }
}
