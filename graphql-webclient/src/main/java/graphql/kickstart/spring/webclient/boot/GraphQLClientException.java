package graphql.kickstart.spring.webclient.boot;

public class GraphQLClientException extends RuntimeException {

  GraphQLClientException(String message, Throwable cause) {
    super(message, cause);
  }

}
