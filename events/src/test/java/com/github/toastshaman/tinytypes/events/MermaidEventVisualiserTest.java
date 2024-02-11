package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.MermaidEventVisualiser.OutputFormat.HTML;
import static com.github.toastshaman.tinytypes.events.MermaidEventVisualiser.OutputFormat.RAW;
import static org.assertj.core.api.Assertions.assertThat;

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
                        FirstEvent-->SecondEvent
                        SecondEvent-->ThirdEvent
                            </pre>
                            <script type="module">
                              import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                            </script>
                          </body>
                        </html>
                        """);
    }

    @Test
    void can_render_as_string() {
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
                                 FirstEvent-->SecondEvent
                                 SecondEvent-->ThirdEvent
                                 """);
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
