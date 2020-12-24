package graphql.kickstart.spring.webclient.boot;

import org.springframework.http.HttpHeaders;

public interface GraphQLRequest {

  static GraphQLRequestBuilder builder() {
    return new GraphQLRequestBuilder();
  }

  GraphQLRequestBody getRequestBody();

  HttpHeaders getHeaders();

}
