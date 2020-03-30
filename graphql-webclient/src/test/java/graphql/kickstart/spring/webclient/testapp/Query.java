package graphql.kickstart.spring.webclient.testapp;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
class Query implements GraphQLQueryResolver {

  String test() {
    return "test";
  }

  String noResponse() {
    return null;
  }

  String echo(String value) {
    return value;
  }

  Simple simple(String id) {
    return new Simple(id);
  }

}
