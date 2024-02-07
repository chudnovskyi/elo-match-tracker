package com.template;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.wait.strategy.Wait.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
public class EndToEndTests {

  private static final String APP = "app_1";
  private static final Integer API_PORT = 8080;
  private static final Integer SERVICE_PORT = 9090;

  private static final String POSTGRES = "postgres_1";
  private static final Integer POSTGRES_PORT = 5432;

  @Container
  @SuppressWarnings("resource")
  private static final DockerComposeContainer<?> COMPOSITION =
      new DockerComposeContainer<>(new File("src/test/end2end/resources/docker/docker-compose.yml"))
          .withExposedService(APP, API_PORT, forHealthcheck())
          .withExposedService(APP, SERVICE_PORT)
          .withExposedService(
              POSTGRES,
              POSTGRES_PORT,
              forLogMessage(".*database system is ready to accept connections.*\\s", 1))
          .withLogConsumer(POSTGRES, new Slf4jLogConsumer(log).withPrefix(POSTGRES))
          .withLogConsumer(APP, new Slf4jLogConsumer(log).withPrefix(APP));

  @Test
  void containerIsRunning() {
    assertThat(COMPOSITION.getContainerByServiceName(APP).orElseThrow().isHealthy()).isTrue();
  }

  @Test
  void metricsAreAvailable() {
    HttpResponse<String> httpResponse = getString("/actuator/metrics", SERVICE_PORT);

    assertThat(httpResponse.statusCode()).isEqualTo(200);
    assertThat(httpResponse.body()).contains("jvm.memory.used");
  }

  @Test
  void actuatorInfoIsAvailable() {
    HttpResponse<String> httpResponse = getString("/actuator/info", SERVICE_PORT);

    assertThat(httpResponse.statusCode()).isEqualTo(200);
    assertThat(httpResponse.body()).contains("java-spring-template");
  }

  @Test
  void userControllerWorks() {
    HttpResponse<String> response =
        postString(
            "/api/v1/users/",
            """
                 {
                   "firstName": "John",
                   "lastName": "Doe"
                 }
                 """);

    assertThat(response.statusCode()).isEqualTo(200);
  }

  @Test
  void swaggerWorks() {
    HttpResponse<String> httpResponse = getString("/swagger-ui/index.html");

    assertThat(httpResponse.statusCode()).isEqualTo(200);
    assertThat(httpResponse.body()).contains("Swagger UI");
  }

  private HttpResponse<String> getString(String path) {
    return getString(path, API_PORT);
  }

  private HttpResponse<String> getString(String path, Integer port) {
    try {
      URI uri =
          new URI(
              "http",
              null,
              COMPOSITION.getServiceHost(APP, port),
              COMPOSITION.getServicePort(APP, port),
              path,
              null,
              null);

      HttpRequest request = newBuilder(uri).GET().timeout(of(10, SECONDS)).build();
      log.debug("Request: {}", request);

      HttpResponse<String> httpResponse = newHttpClient().send(request, ofString());
      log.info("Response: {}", httpResponse);

      return httpResponse;
    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpResponse<String> postString(String path, String body) {
    return postString(path, API_PORT, body, "application/json");
  }

  private HttpResponse<String> postString(
      String path, Integer port, String body, String contentType) {

    try {
      URI uri =
          new URI(
              "http",
              null,
              COMPOSITION.getServiceHost(APP, port),
              COMPOSITION.getServicePort(APP, port),
              path,
              null,
              null);

      HttpRequest request =
          newBuilder(uri)
              .POST(ofString(body))
              .timeout(of(10, SECONDS))
              .header("Content-Type", contentType)
              .build();
      log.debug("Request: {}", request);

      HttpResponse<String> httpResponse = newHttpClient().send(request, ofString());
      log.info("Response: {}", httpResponse);
      log.debug("Response body: {}", httpResponse.body());

      return httpResponse;
    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
