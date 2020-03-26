package graphql.kickstart.spring.webclient.boot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GraphQLWebClientTest {

  @LocalServerPort
  private int randomServerPort;

  private GraphQLWebClient graphqlClient;

  @BeforeEach
  void beforeEach() {
    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:" + randomServerPort)
        .build();
    graphqlClient = new GraphQLWebClientImpl(webClient);
  }

  @Test
  void queryWithoutVariablesSucceeds() {
    Mono<String> response = graphqlClient.query("query-test.graphql", null, String.class);
    assertNotNull("response should not be null", response);
    assertEquals("response should equal 'test'", "test", response.block());
  }

}
