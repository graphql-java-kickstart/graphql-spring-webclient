package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class GraphQLErrorsException extends RuntimeException {

  List<GraphQLError> errors;

  @Override
  public String getMessage() {
    return errors.get(0).getMessage();
  }

}
