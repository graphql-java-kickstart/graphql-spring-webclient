package graphql.kickstart.spring.webclient.testapp;

import static java.util.Collections.singletonList;

import graphql.kickstart.spring.GraphQLSpringContext;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
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

  String header(String name, DataFetchingEnvironment env) {
    GraphQLSpringContext context = env.getContext();
    List<String> headers = context.getServerWebExchange().getRequest().getHeaders().get(name);
    if (headers != null && !headers.isEmpty()) {
      return headers.get(0);
    }
    return null;
  }

}
