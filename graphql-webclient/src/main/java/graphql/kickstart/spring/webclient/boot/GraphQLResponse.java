package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;

@Data
class GraphQLResponse {

  private Map<String, Object> data;
  private List<String> errors;

  <T> T getFirstObject(Class<T> type) {
    return (T) data.entrySet().stream().findFirst().map(Entry::getValue).orElseThrow();
  }

}
