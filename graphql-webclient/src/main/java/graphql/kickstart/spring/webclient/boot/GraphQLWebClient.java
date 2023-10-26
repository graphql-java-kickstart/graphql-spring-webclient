package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public interface GraphQLWebClient {

  static GraphQLWebClient newInstance(WebClient webClient, ObjectMapper objectMapper) {
    return GraphQLWebClient.newInstance(webClient, objectMapper, null);
  }

  static GraphQLWebClient newInstance(WebClient webClient, ObjectMapper objectMapper, Retry retry) {
    return new GraphQLWebClientImpl(webClient, objectMapper, retry);
  }

  <T> Mono<T> post(String resource, Class<T> returnType);

  <T> Mono<T> post(String resource, Map<String, Object> variables, Class<T> returnType);

  Mono<GraphQLResponse> post(GraphQLRequest request);

  <T> Flux<T> flux(String resource, Class<T> returnType);

  <T> Flux<T> flux(String resource, Map<String, Object> variables, Class<T> returnType);

}
