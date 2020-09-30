package graphql.kickstart.spring.webclient.boot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;

public class GraphQLRequestBuilder<T> {

  private final HttpHeaders headers = new HttpHeaders();
  private final GraphQLRequestBody.GraphQLRequestBodyBuilder bodyBuilder = GraphQLRequestBody.builder();
  private final Class<T> returnType;

  GraphQLRequestBuilder(Class<T> returnType) {
    this.returnType = returnType;
  }

  GraphQLRequestBuilder<T> header(String name, String value) {
    headers.add(name, value);
    return this;
  }

  GraphQLRequestBuilder<T> header(String name, String... values) {
    headers.addAll(name, Arrays.asList(values));
    return this;
  }

  GraphQLRequestBuilder<T> resource(String resource) {
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

  GraphQLRequestBuilder<T> query(String query) {
    bodyBuilder.query(query);
    return this;
  }

  GraphQLRequestBuilder<T> variables(Object variables) {
    bodyBuilder.variables(variables);
    return this;
  }

  GraphQLRequestBuilder<T> operationName(String operationName) {
    bodyBuilder.operationName(operationName);
    return this;
  }

  GraphQLRequest<T> build() {
    return new GraphQLRequestImpl<>(headers, bodyBuilder.build(), returnType);
  }

}
