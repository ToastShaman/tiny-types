package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOutputFormat.HTML;
import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOutputFormat.RAW;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.EventFilter;
import com.github.toastshaman.tinytypes.events.Events;
import java.io.StringWriter;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MermaidEventVisualiserTest {

    @Test
    void can_render_as_html() {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var output = new StringWriter();
        mermaid.render(HTML, output);

        assertThat(output.toString())
                .isEqualTo(
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                          <body>
                            <pre class="mermaid">
                        flowchart TB
                        FirstEvent[I am the first event]
                        SecondEvent((SecondEvent))
                        ThirdEvent[/ThirdEvent\\]
                        FirstEvent --> SecondEvent
                        SecondEvent --> ThirdEvent
                            </pre>
                            <script src="https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.8.0/mermaid.min.js" integrity="sha512-LjhAzAg2/5zsgtj/a6rKlyripQs594DkfL+vlA0qwrb/0McmLqIpglzZmrfAdbBURFS7LhFC2yvDDhilSx9UDg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
                          </body>
                        </html>
                        """);
    }

    @Test
    void can_render_single_node_as_string() {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        var output = new StringWriter();
        mermaid.render(RAW, output);

        assertThat(output.toString())
                .isEqualTo(
                        """
                                 flowchart TB
                                 FirstEvent[I am the first event]

                                 """);
    }

    @Test
    void can_render_multiple_nodes_as_string() {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var output = new StringWriter();
        mermaid.render(RAW, output);

        assertThat(output.toString())
                .isEqualTo(
                        """
                                 flowchart TB
                                 FirstEvent[I am the first event]
                                 SecondEvent((SecondEvent))
                                 ThirdEvent[/ThirdEvent\\]
                                 FirstEvent --> SecondEvent
                                 SecondEvent --> ThirdEvent
                                 """);
    }

    @Test
    void can_preview() {
        var mermaid = new MermaidEventVisualiser();
        var events = new MermaidStylingFilter().filter(mermaid);

        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var preview = mermaid.liveEditor();

        assertThat(preview)
                .hasToString(
                        "https://mermaid.live/edit#base64:eyJjb2RlIjoiZmxvd2NoYXJ0IFRCXG5GaXJzdEV2ZW50W0kgYW0gdGhlIGZpcnN0IGV2ZW50XVxuU2Vjb25kRXZlbnQoKFNlY29uZEV2ZW50KSlcblRoaXJkRXZlbnRbL1RoaXJkRXZlbnRcXF1cbkZpcnN0RXZlbnQgLS0-IFNlY29uZEV2ZW50XG5TZWNvbmRFdmVudCAtLT4gVGhpcmRFdmVudFxuIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRhcmsifX0=");
    }

    private static class FirstEvent implements Event {}

    private static class SecondEvent implements Event {}

    private static class ThirdEvent implements Event {}

    private static class MermaidStylingFilter implements EventFilter {

        @Override
        public Events filter(Events next) {
            return event -> next.record(
                    switch (event) {
                        case FirstEvent e -> e.addMetadata("mermaid_node_text", "I am the first event");
                        case SecondEvent e -> e.addMetadata("mermaid_node_shape", "circle");
                        case ThirdEvent e -> e.addMetadata("mermaid_node_shape", "trapezoid");
                        default -> event;
                    });
        }
    }
}
