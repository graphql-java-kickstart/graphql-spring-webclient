package graphql.kickstart.spring.webclient.testapp;

import static java.util.Collections.singletonList;

import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
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

  List<Simple> list() {
    return singletonList(simple("1"));
  }

}
