package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    return execute(resource, variables).map(it -> readValue(it, returnType));
  }

  @SneakyThrows
  private <T> T readValue(Object value, Class<T> returnType) {
    log.trace("Read value: {}", value);
    return objectMapper.convertValue(value, returnType);
  }

  @Override
  public <T> Mono<T> post(GraphQLRequest<T> request) {
    return execute(request).map(it -> readValue(it, request.getReturnType()));
  }

  @Override
  public <T> Flux<T> flux(String resource, Class<T> returnType) {
    return flux(resource, null, returnType);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Flux<T> flux(String resource, Map<String, Object> variables, Class<T> returnType) {
    Mono<Object> responseObject = execute(resource, variables);

    return responseObject.map(List.class::cast)
        .flatMapMany(Flux::fromIterable)
        .map(it -> readValue(it, returnType));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Flux<T> flux(GraphQLRequest<T> request) {
    Mono<Object> responseObject = execute(request);
    return responseObject.map(List.class::cast)
        .flatMapMany(Flux::fromIterable)
        .map(it -> readValue(it, request.getReturnType()));
  }

  private Mono<Object> execute(String resource, Map<String, Object> variables) {
    GraphQLRequest<?> request = GraphQLRequest.builder(Object.class)
        .resource(resource)
        .variables(variables)
        .build();
    return execute(request);
  }

  private Mono<Object> execute(GraphQLRequest<?> request) {
    WebClient.RequestBodySpec spec = webClient.post()
        .contentType(MediaType.APPLICATION_JSON);
    request.getHeaders().forEach((header, values) -> spec.header(header, values.toArray(new String[0])));
    return spec.bodyValue(request.getRequestBody())
        .retrieve()
        .bodyToMono(GraphQLResponse.class)
        .flatMap(it -> Mono.justOrEmpty(it.getFirstObject()));
  }

}
