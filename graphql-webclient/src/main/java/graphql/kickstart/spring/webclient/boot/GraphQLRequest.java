package graphql.kickstart.spring.webclient.boot;

import org.springframework.http.HttpHeaders;

public interface GraphQLRequest<T> {

  static GraphQLRequestBuilder<Object> builder() {
    return new GraphQLRequestBuilder<>(Object.class);
  }

  static <T> GraphQLRequestBuilder<T> builder(Class<T> returnType) {
    return new GraphQLRequestBuilder<>(returnType);
  }

  GraphQLRequestBody getRequestBody();

  HttpHeaders getHeaders();

  Class<T> getReturnType();

}
