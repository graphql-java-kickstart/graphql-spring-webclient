package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;

@Data
class GraphQLResponse {

  private Map<String, Object> data;
  private List<GraphQLError> errors;

  Object getFirstObject() {
    validateNoErrors();

    if (data != null && !data.isEmpty()) {
      return data.entrySet().stream().findFirst().map(Entry::getValue).orElse(null);
    }
    return null;
  }

  private void validateNoErrors() {
    if (errors != null && !errors.isEmpty()) {
      throw new GraphQLErrorsException(errors);
    }
  }

}
