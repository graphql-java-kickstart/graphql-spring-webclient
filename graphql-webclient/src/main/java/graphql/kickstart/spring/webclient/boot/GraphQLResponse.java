package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import lombok.Data;

@Data
class GraphQLResponse {

  private Map<String, Object> data;
  private List<GraphQLError> errors;

  Object getFirstObject() {
    validateNoErrors();

    if (data != null) {
      return data.entrySet().stream().findFirst().map(Entry::getValue).orElseThrow(NoSuchElementException::new);
    }
    throw new NoSuchElementException();
  }

  private void validateNoErrors() {
    if (errors != null && !errors.isEmpty()) {
      throw new GraphQLErrorsException(errors);
    }
  }

}
