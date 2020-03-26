package graphql.kickstart.spring.webclient.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = GraphQLWebClientImpl.class)
public class GraphQLWebClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return null;
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLWebClient graphQLWebClient(WebClient webClient) {
    return new GraphQLWebClientImpl(webClient);
  }

}
