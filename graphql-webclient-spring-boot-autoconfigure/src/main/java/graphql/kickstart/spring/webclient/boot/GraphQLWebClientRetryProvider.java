package graphql.kickstart.spring.webclient.boot;

import reactor.util.retry.Retry;

public interface GraphQLWebClientRetryProvider {
  Retry get();
}
