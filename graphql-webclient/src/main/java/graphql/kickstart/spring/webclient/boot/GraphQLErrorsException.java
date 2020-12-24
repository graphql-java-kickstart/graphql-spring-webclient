package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class GraphQLErrorsException extends RuntimeException {

  transient List<GraphQLError> errors;
  String message;

  public GraphQLErrorsException(@NonNull List<GraphQLError> errors) {
    if (errors.isEmpty()) {
      throw new IllegalArgumentException("errors must not be empty");
    }
    this.errors = errors;
    this.message = errors.get(0).getMessage();
  }

}
