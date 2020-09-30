package graphql.kickstart.spring.webclient.boot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class GraphQLRequestBody {

  String query;
  Object variables;
  String operationName;

  static GraphQLRequestBodyBuilder builder() {
    return new GraphQLRequestBodyBuilder();
  }

  static class GraphQLRequestBodyBuilder {
    // added this partial builder to let Javadoc play nice with Lombok, see https://stackoverflow.com/a/58809436/12507062
  }

}
