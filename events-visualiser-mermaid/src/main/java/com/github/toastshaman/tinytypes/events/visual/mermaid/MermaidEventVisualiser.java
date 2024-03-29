package com.github.toastshaman.tinytypes.events.visual.mermaid;

import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOrientation.TB;
import static com.github.toastshaman.tinytypes.events.visual.mermaid.MermaidOutputFormat.RAW;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONObject;

public final class MermaidEventVisualiser implements Events {

    private final List<Event> captured = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void record(Event event) {
        lock.lock();
        try {
            captured.add(event);
        } finally {
            lock.unlock();
        }
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
                                <script src="https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.8.0/mermaid.min.js" integrity="sha512-LjhAzAg2/5zsgtj/a6rKlyripQs594DkfL+vlA0qwrb/0McmLqIpglzZmrfAdbBURFS7LhFC2yvDDhilSx9UDg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
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

        var graph = MermaidEventsGraph.from(captured);
        var nodes = String.join("\n", graph.nodesWithText());
        var links = String.join("\n", graph.nodesWithLink());
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
