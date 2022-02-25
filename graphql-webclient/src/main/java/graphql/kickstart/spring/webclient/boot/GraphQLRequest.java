package graphql.kickstart.spring.webclient.boot;

import org.springframework.http.HttpHeaders;

import java.util.Map;

public interface GraphQLRequest {

  static GraphQLRequestBuilder builder() {
    return new GraphQLRequestBuilder();
  }

  GraphQLRequestBody getRequestBody();

  HttpHeaders getHeaders();

  Map<String, Object> getAttributes();

}
