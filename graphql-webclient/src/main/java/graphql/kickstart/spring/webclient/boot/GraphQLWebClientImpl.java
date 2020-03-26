package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
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
    GraphQLRequest request = GraphQLRequest.builder()
        .query(loadQuery(resource))
        .variables(variables)
        .build();

    return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(GraphQLResponse.class)
        .map(GraphQLResponse::getFirstObject)
        .map(it -> readValue(it, returnType));
  }

  @SneakyThrows
  private <T> T readValue(Object value, Class<T> returnType) {
    log.debug("Read value: '{}'", value);
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

}
