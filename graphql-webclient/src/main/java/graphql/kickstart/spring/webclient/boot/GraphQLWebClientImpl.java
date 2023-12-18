package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
class GraphQLWebClientImpl implements GraphQLWebClient {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final Retry retry;

  @Override
  public <T> Mono<T> post(String resource, Class<T> returnType) {
    return post(resource, null, returnType);
  }

  @Override
  public <T> Mono<T> post(String resource, Map<String, Object> variables, Class<T> returnType) {
    return tryRetry(
      post(resource, variables)
          .flatMap(it -> {
            it.validateNoErrors();
            return Mono.justOrEmpty(it.getFirst(returnType));
          })
    );
  }

  @Override
  public Mono<GraphQLResponse> post(GraphQLRequest request) {
    return tryRetry(postWithoutRetry(request));
  }

  @Override
  public <T> Flux<T> flux(String resource, Class<T> returnType) {
    return flux(resource, null, returnType);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Flux<T> flux(String resource, Map<String, Object> variables, Class<T> returnType) {
    return tryRetry(
      post(resource, variables)
          .map(it -> it.getFirstList(returnType))
          .flatMapMany(Flux::fromIterable)
    );
  }

  private Mono<GraphQLResponse> post(String resource, Map<String, Object> variables) {
    GraphQLRequest request = GraphQLRequest.builder()
        .resource(resource)
        .variables(variables)
        .build();
    return postWithoutRetry(request);
  }

  private Mono<GraphQLResponse> postWithoutRetry(GraphQLRequest request) {
    WebClient.RequestBodySpec spec = webClient.post().contentType(MediaType.APPLICATION_JSON);
    request.getAttributes()
        .forEach(spec::attribute);
    request.getHeaders()
        .forEach((header, values) -> spec.header(header, values.toArray(new String[0])));
    return spec.bodyValue(request.getRequestBody())
        .retrieve()
        .bodyToMono(String.class)
        .map(it -> new GraphQLResponse(it, objectMapper));
  }

  private <T> Mono<T> tryRetry(Mono<T> publisher) {
    return (retry == null ? publisher : publisher.retryWhen(retry));
  }

  private <T> Flux<T> tryRetry(Flux<T> publisher) {
    return (retry == null ? publisher : publisher.retryWhen(retry));
  }

}
