package graphql.kickstart.spring.webclient.boot;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLError {

  private String message;
  private List<Location> locations;
  private List<String> path;
  private Map<String, Object> extensions;

}
