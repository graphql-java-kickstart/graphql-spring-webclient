package graphql.kickstart.spring.webclient.boot;

import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GraphQLWebClient {

  <T> Mono<T> post(String resource, Class<T> returnType);

  <T> Mono<T> post(String resource, Map<String, Object> variables, Class<T> returnType);

  <T> Flux<T> flux(String resource, Class<T> returnType);

  <T> Flux<T> flux(String resource, Map<String, Object> variables, Class<T> returnType);

}
