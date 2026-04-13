package com.github.toastshaman.tinytypes.aws;

import java.net.URI;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;

public class MiniStackContainer extends GenericContainer<MiniStackContainer> {

    private static final int PORT = 4566;

    private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse("ministackorg/ministack");

    public MiniStackContainer() {
        this("latest");
    }

    public MiniStackContainer(String tag) {
        this(DEFAULT_IMAGE.withTag(tag));
    }

    public MiniStackContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(PORT);
        waitingFor(Wait.forHttp("/_ministack/health").forPort(PORT).forStatusCode(200));
    }

    public URI getEndpoint() {
        return URI.create("http://%s:%d".formatted(getHost(), getMappedPort(PORT)));
    }

    public int getPort() {
        return getMappedPort(PORT);
    }

    public String getAccessKey() {
        return "test";
    }

    public String getSecretKey() {
        return "test";
    }

    public String getRegion() {
        return Region.EU_WEST_2.toString();
    }
}
