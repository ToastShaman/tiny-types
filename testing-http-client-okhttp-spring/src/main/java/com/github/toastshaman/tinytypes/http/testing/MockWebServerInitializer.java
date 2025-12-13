package com.github.toastshaman.tinytypes.http.testing;

import java.util.Map;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Creates a MockWebServer, starts, and registers it as a singleton in Spring's application context.
 * The server's port is used to construct a host URL, which is then used to set a property in the
 * application context. The code also registers a listener for the ContextClosedEvent that shuts
 * down the server when the event is received.
 *
 * <p>Overall, this code initializes a mock web server and sets it up to be used in the application.
 *
 * <p>Usage: {@code @ContextConfiguration(initializers = MockWebServerInitializer.class)}
 */
@SuppressWarnings("NullableProblems")
public class MockWebServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            var server = new MockWebServer();

            server.start();

            applicationContext.addApplicationListener(createShutdownListener(server));

            var host = server.url("/").toString();

            var map = Map.of("mock.host", host, "mock.port", String.valueOf(server.getPort()));

            TestPropertyValues.of(map).applyTo(applicationContext);

            applicationContext.getBeanFactory().registerSingleton("mockWebServer", server);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start MockWebServer", e);
        }
    }

    private ApplicationListener<ContextClosedEvent> createShutdownListener(MockWebServer server) {
        return _ -> {
            try {
                server.shutdown();
            } catch (Exception e) {
                /* do nothing */
            }
        };
    }
}
