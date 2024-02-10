package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.MermaidEventRenderer.MermaidOutputFormat.HTML;
import static com.github.toastshaman.tinytypes.events.MermaidEventRenderer.MermaidOutputFormat.RAW;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MermaidEventRendererTest {

    @Test
    void can_render_as_html() {
        var mermaid = new MermaidEventRenderer();

        var events = new PrintStreamEventLogger(System.out).and(mermaid);
        events.record(new FirstEvent());
        events.record(new SecondEvent());
        events.record(new ThirdEvent());

        var output = new StringWriter();
        mermaid.render(HTML, output);

        assertThat(output.toString())
                .startsWith("<!DOCTYPE html>")
                .contains("flowchart TB")
                .contains("FirstEvent --> SecondEvent --> ThirdEvent");
    }

    @Test
    void can_render_as_string() {
        var mermaid = new MermaidEventRenderer();

        var events = new PrintStreamEventLogger(System.out).and(mermaid);
        events.record(new FirstEvent().addMetadata("mermaid_node_text", "Start"));
        events.record(new SecondEvent().addMetadata("mermaid_node_shape", "circle"));
        events.record(new ThirdEvent().addMetadata("mermaid_node_shape", "trapezoid"));

        var output = new StringWriter();
        mermaid.render(RAW, output);

        assertThat(output.toString()).startsWith("flowchart TB").contains("FirstEvent --> SecondEvent --> ThirdEvent");
    }

    private static class FirstEvent implements Event {}

    private static class SecondEvent implements Event {}

    private static class ThirdEvent implements Event {}
}
