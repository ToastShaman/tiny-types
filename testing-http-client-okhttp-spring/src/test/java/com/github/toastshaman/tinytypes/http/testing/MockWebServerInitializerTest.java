package com.github.toastshaman.tinytypes.http.testing;

import static org.assertj.core.api.Assertions.assertThat;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = MockWebServerInitializer.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class MockWebServerInitializerTest {

    @Value("${mock.host}")
    private String host;

    @Value("${mock.port}")
    private int port;

    @Autowired
    private MockWebServer server;

    @Test
    void can_inject_mock_web_server_properties() {
        assertThat(host).startsWith("http://localhost:");
        assertThat(port).isGreaterThan(0);
        assertThat(server).isNotNull();
    }

    @SpringBootApplication
    public static class MyTestApp {
        public static void main(String[] args) {
            SpringApplication.run(MyTestApp.class, args);
        }
    }
}
