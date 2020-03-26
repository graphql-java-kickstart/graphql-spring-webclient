package graphql.kickstart.spring.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
public class GraphQLWebClient {

  private final WebClient webClient;

}
