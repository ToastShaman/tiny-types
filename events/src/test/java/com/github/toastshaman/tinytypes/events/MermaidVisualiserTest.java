package com.github.toastshaman.tinytypes.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MermaidVisualiserTest {

    @Test
    void can_render_as_html() {
        var visualiser = new MermaidVisualiser();
        visualiser.record(new FirstEvent());
        visualiser.record(new SecondEvent());
        visualiser.record(new ThirdEvent());

        var diagram = visualiser.asHtml();

        assertThat(diagram)
                .startsWith("<!DOCTYPE html>")
                .contains("flowchart TB")
                .contains("FirstEvent --> SecondEvent --> ThirdEvent");
    }

    @Test
    void can_render_as_string() {
        var visualiser = new MermaidVisualiser();
        visualiser.record(new FirstEvent());
        visualiser.record(new SecondEvent());
        visualiser.record(new ThirdEvent());

        var diagram = visualiser.asString();

        assertThat(diagram).startsWith("flowchart TB").contains("FirstEvent --> SecondEvent --> ThirdEvent");
    }

    static class FirstEvent implements Event {}

    static class SecondEvent implements Event {}

    static class ThirdEvent implements Event {}
}
