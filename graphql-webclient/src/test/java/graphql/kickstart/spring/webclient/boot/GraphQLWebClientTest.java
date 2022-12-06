package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.spring.webclient.testapp.Simple;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GraphQLWebClientTest {

  @LocalServerPort
  private int randomServerPort;

  @Autowired
  private ObjectMapper objectMapper;

  private GraphQLWebClient graphqlClient;

  @BeforeEach
  void beforeEach() {
    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:" + randomServerPort + "/graphql")
        .build();
    graphqlClient = GraphQLWebClient.newInstance(webClient, objectMapper);
  }

  @Test
  @DisplayName("Query test without variables and returning String")
  void queryWithoutVariablesSucceeds() {
    Mono<String> response = graphqlClient.post("query-test.graphql", null, String.class);
    assertNotNull(response, "response should not be null");
    assertEquals("test", response.block(), "response should equal 'test'");
  }

  @Test
  @DisplayName("Query echo with String variable and returning String")
  void echoStringSucceeds() {
    Mono<String> response = graphqlClient
        .post("query-echo.graphql", Map.of("value", "echo echo echo"), String.class);
    assertNotNull(response, "response should not be null");
    assertEquals("echo echo echo", response.block());
  }

  @Test
  @DisplayName("Query simple return type")
  void simpleTypeSucceeds() {
    Mono<Simple> response = graphqlClient
        .post("query-simple.graphql", Map.of("id", "my-id"), Simple.class);
    assertNotNull(response, "response should not be null");
    Simple object = response.block();
    assertNotNull(object);
    assertEquals("my-id", object.getId(), "response id should equal 'my-id'");
  }

  @Test
  void simpleTypeAsRequestSucceeds() {
    GraphQLRequest request = GraphQLRequest.builder()
        .resource("query-simple.graphql")
        .variables(Map.of("id", "my-id"))
        .build();
    Mono<GraphQLResponse> response = graphqlClient.post(request);
    assertNotNull(response, "response should not be null");
    GraphQLResponse object = response.block();
    assertNotNull(object);
    Simple simple = object.get("simple", Simple.class);
    assertEquals("my-id", simple.getId(), "response id should equal 'my-id'");
  }

  @Test
  void errorResponseSucceeds() {
    Mono<String> response = graphqlClient.post("error.graphql", String.class);
    assertThrows(GraphQLErrorsException.class, response::block);
  }

  @Test
  void noResponseSucceeds() {
    Mono<String> response = graphqlClient.post("query-noResponse.graphql", String.class);
    Optional<String> noResponse = response.blockOptional();
    assertTrue("response should be empty", noResponse.isEmpty());
  }

  @Test
  void listSucceeds() {
    Flux<Simple> response = graphqlClient.flux("query-list.graphql", Simple.class);
    List<Simple> list = response.collectList().block();
    assertNotNull(list);
    assertEquals(1, list.size());
  }

  @Test
  void headerIsAdded() {
    GraphQLRequest request = GraphQLRequest.builder()
        .resource("query-header.graphql")
        .variables(Map.of("name", "my-custom-header"))
        .header("my-custom-header", "my-custom-header-value")
        .build();
    Mono<GraphQLResponse> response = graphqlClient.post(request);
    assertNotNull(response, "response should not be null");
    GraphQLResponse object = response.block();
    assertNotNull(object);
    assertEquals("my-custom-header-value",
        object.get("header", String.class), "response should equal 'my-custom-header-value'");
  }

}
