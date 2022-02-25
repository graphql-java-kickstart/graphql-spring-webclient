package graphql.kickstart.spring.webclient.boot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;

public class GraphQLRequestBuilder {

  private final Map<String, Object> attributes = new LinkedHashMap<>();
  private final HttpHeaders headers = new HttpHeaders();
  private final GraphQLRequestBody.GraphQLRequestBodyBuilder bodyBuilder = GraphQLRequestBody.builder();

  GraphQLRequestBuilder() {
  }

  public GraphQLRequestBuilder attribute(String name, Object value) {
    attributes.put(name, value);
    return this;
  }

  public GraphQLRequestBuilder header(String name, String... values) {
    headers.addAll(name, Arrays.asList(values));
    return this;
  }

  public GraphQLRequestBuilder resource(String resource) {
    return query(loadQuery(resource));
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

  public GraphQLRequestBuilder query(String query) {
    bodyBuilder.query(query);
    return this;
  }

  public GraphQLRequestBuilder variables(Object variables) {
    bodyBuilder.variables(variables);
    return this;
  }

  public GraphQLRequestBuilder operationName(String operationName) {
    bodyBuilder.operationName(operationName);
    return this;
  }

  public GraphQLRequest build() {
    return new GraphQLRequestImpl(attributes, headers, bodyBuilder.build());
  }

}
