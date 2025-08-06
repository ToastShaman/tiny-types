package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOutputFormat.HTML;
import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOutputFormat.RAW;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.EventFilter;
import com.github.toastshaman.tinytypes.events.Events;
import com.oneeyedmen.okeydoke.Approver;
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MermaidEventVisualiserTest {

    @RegisterExtension
    ApprovalsExtension approvals = new ApprovalsExtension();

    @Test
    void can_render_as_html(Approver approver) {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var output = mermaid.renderToString(HTML);

        approver.assertApproved(output);
    }

    @Test
    void can_render_single_node_as_string(Approver approver) {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());

        var output = mermaid.renderToString(RAW);

        approver.assertApproved(output);
    }

    @Test
    void can_render_multiple_nodes_as_string(Approver approver) {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var output = mermaid.renderToString(RAW);

        approver.assertApproved(output);
    }

    @Test
    void can_preview(Approver approver) {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var preview = mermaid.liveEditor();

        approver.assertApproved(preview);
    }

    private static class FirstEvent implements Event {}

    private static class SecondEvent implements Event {}

    private static class ThirdEvent implements Event {}

    private static class MermaidStylingFilter implements EventFilter {

        @Override
        public Events filter(Events next) {
            return event -> next.record(
                    switch (event) {
                        case FirstEvent e ->
                            e.addMetadata("mermaid_node_text", "I am the first event")
                                    .addMetadata("mermaid_outgoing_edge_text", "I am the first label");
                        case SecondEvent e ->
                            e.addMetadata("mermaid_node_shape", "circle")
                                    .addMetadata("mermaid_outgoing_edge_text", "I am the second label");
                        case ThirdEvent e ->
                            e.addMetadata("mermaid_node_shape", "trapezoid")
                                    .addMetadata("mermaid_incoming_edge_text", "I am the third label");
                        default -> event;
                    });
        }
    }
}
