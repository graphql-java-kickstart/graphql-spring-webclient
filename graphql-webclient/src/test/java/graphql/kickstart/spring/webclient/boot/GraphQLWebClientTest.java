package graphql.kickstart.spring.webclient.boot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.spring.webclient.testapp.Simple;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
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
    graphqlClient = new GraphQLWebClientImpl(webClient, objectMapper);
  }

  @Test
  @DisplayName("Query test without variables and returning String")
  void queryWithoutVariablesSucceeds() {
    Mono<String> response = graphqlClient.post("query-test.graphql", null, String.class);
    assertNotNull("response should not be null", response);
    assertEquals("response should equal 'test'", "test", response.block());
  }

  @Test
  @DisplayName("Query echo with String variable and returning String")
  void echoStringSucceeds() {
    Mono<String> response = graphqlClient.post("query-echo.graphql", Map.of("value", "echo echo echo"), String.class);
    assertNotNull("response should not be null", response);
    assertEquals("response should equal 'echo echo echo'", "echo echo echo", response.block());
  }

  @Test
  @DisplayName("Query simple return type")
  void simpleTypeSucceeds() {
    Mono<Simple> response = graphqlClient.post("query-simple.graphql", Map.of("id", "my-id"), Simple.class);
    assertNotNull("response should not be null", response);
    Simple object = response.block();
    assertNotNull(object);
    assertEquals("response id should equal 'my-id'", "my-id", object.getId());
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

}
