package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
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

  @SneakyThrows
  private String loadQuery(String path) {
    return loadResource(new ClassPathResource(path));
  }

  private String loadResource(Resource resource) throws IOException {
    try (InputStream inputStream = resource.getInputStream()) {
      return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }
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

  private Mono<Object> execute(String resource, Map<String, Object> variables) {
    GraphQLRequest request = GraphQLRequest.builder()
        .query(loadQuery(resource))
        .variables(variables)
        .build();

    return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(GraphQLResponse.class)
        .flatMap(it -> Mono.justOrEmpty(it.getFirstObject()));
  }

}
