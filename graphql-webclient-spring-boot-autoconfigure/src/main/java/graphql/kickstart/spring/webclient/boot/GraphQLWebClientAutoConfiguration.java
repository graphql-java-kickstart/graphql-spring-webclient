package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@ComponentScan(basePackageClasses = GraphQLWebClientImpl.class)
public class GraphQLWebClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLWebClient graphQLWebClient(WebClient webClient, ObjectMapper objectMapper) {
    return new GraphQLWebClientImpl(webClient, objectMapper);
  }

}
