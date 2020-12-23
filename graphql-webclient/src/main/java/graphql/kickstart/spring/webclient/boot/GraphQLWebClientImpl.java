package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
class GraphQLWebClientImpl implements GraphQLWebClient {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  @Override
  public <T> Mono<T> post(String resource, Class<T> returnType) {
    return post(resource, null, returnType);
  }

  @Override
  public <T> Mono<T> post(String resource, Map<String, Object> variables, Class<T> returnType) {
    return post(resource, variables)
        .flatMap(it -> {
          it.validateNoErrors();
          return Mono.justOrEmpty(it.getFirstObject(returnType));
        });
  }

  @Override
  public Mono<GraphQLResponse> post(GraphQLRequest request) {
    WebClient.RequestBodySpec spec = webClient.post().contentType(MediaType.APPLICATION_JSON);
    request.getHeaders()
        .forEach((header, values) -> spec.header(header, values.toArray(new String[0])));
    return spec.bodyValue(request.getRequestBody())
        .retrieve()
        .bodyToMono(String.class)
        .map(it -> new GraphQLResponse(it, objectMapper));
  }

  @Override
  public <T> Flux<T> flux(String resource, Class<T> returnType) {
    return flux(resource, null, returnType);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Flux<T> flux(String resource, Map<String, Object> variables, Class<T> returnType) {
    return post(resource, variables)
        .map(it -> it.getFirstList(returnType))
        .flatMapMany(Flux::fromIterable);
  }

  private Mono<GraphQLResponse> post(String resource, Map<String, Object> variables) {
    GraphQLRequest request = GraphQLRequest.builder()
        .resource(resource)
        .variables(variables)
        .build();
    return post(request);
  }

}
