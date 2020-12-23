package graphql.kickstart.spring.webclient.boot;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GraphQLClientException extends RuntimeException {

  GraphQLClientException(String message, Throwable cause) {
    super(message, cause);
  }

}
