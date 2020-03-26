package graphql.kickstart.spring.webclient.boot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class GraphQLRequest {

  String query;
  Object variables;
  String operationName;

}
