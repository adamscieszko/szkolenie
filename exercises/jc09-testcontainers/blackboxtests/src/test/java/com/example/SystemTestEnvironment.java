package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.client.WireMockBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;
import java.util.Objects;

public class SystemTestEnvironment {

    private static final Logger log = LoggerFactory.getLogger(SystemTestEnvironment.class);

    private static final String SERVICE_CONTAINER = "service_1";
    private static final int SERVICE_PORT = 8080;
    private static final String WIREMOCK_CONTAINER = "wiremock_1";
    private static final int WIREMOCK_PORT = 8080;

    private static boolean environmentStarted = false;

    private static DockerComposeContainer environment =
            new DockerComposeContainer(getDockerComposeFile())
                    .withLocalCompose(true)
                    .withLogConsumer(SERVICE_CONTAINER, new Slf4jLogConsumer(log).withPrefix(SERVICE_CONTAINER))
                    .withExposedService(SERVICE_CONTAINER, SERVICE_PORT, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(120)))
                    .withExposedService(WIREMOCK_CONTAINER, WIREMOCK_PORT, Wait.forListeningPort());

    private SystemTestEnvironment() {
    }

    static void start() {
        if (!environmentStarted) {
            environmentStarted = true;
            environment.start();
            Runtime.getRuntime().addShutdownHook(new Thread(environment::stop));
        }
    }

    private static File getDockerComposeFile() {
        ClassLoader classLoader = SystemTestEnvironment.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource("docker-compose.yml")).getFile());
    }

    static String serviceUrl() {
        return String.format("http://%s:%d",
                environment.getServiceHost(SERVICE_CONTAINER, SERVICE_PORT),
                environment.getServicePort(SERVICE_CONTAINER, SERVICE_PORT));
    }

    static WireMock wireMock() {
        return new WireMockBuilder()
                .host(environment.getServiceHost(WIREMOCK_CONTAINER, WIREMOCK_PORT))
                .port(environment.getServicePort(WIREMOCK_CONTAINER, WIREMOCK_PORT))
                .build();
    }

}
