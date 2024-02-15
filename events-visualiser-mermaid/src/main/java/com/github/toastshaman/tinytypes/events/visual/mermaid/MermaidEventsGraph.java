package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

import com.github.toastshaman.tinytypes.events.Event;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public record MermaidEventsGraph(ValueGraph<MermaidNode, MermaidEdgeLabel> graph) {

    public MermaidEventsGraph {
        requireNonNull(graph);
    }

    public List<String> nodesWithText() {
        return graph.nodes().stream()
                .sorted(comparing(MermaidNode::id))
                .map(MermaidNode::render)
                .toList();
    }

    public List<String> nodesWithLink() {
        return graph.edges().stream()
                .sorted(comparing(it -> it.source().id()))
                .map(it -> {
                    var label = graph.edgeValue(it).orElse(MermaidEdgeLabel.empty());
                    return label.render(it.source(), it.target());
                })
                .toList();
    }

    public static MermaidEventsGraph from(List<Event> events) {
        var graph = ValueGraphBuilder.directed().<MermaidNode, MermaidEdgeLabel>build();

        if (events.size() == 1) {
            graph.addNode(MermaidNode.of(events.getFirst()));
        }

        for (int i = 0; i < events.size() - 1; i++) {
            var first = MermaidNode.of(events.get(i));
            var second = MermaidNode.of(events.get(i + 1));
            var label = MermaidEdgeLabel.from(first, second);

            graph.addNode(first);
            graph.addNode(second);
            graph.putEdgeValue(first, second, label);
        }

        return new MermaidEventsGraph(graph);
    }
}
