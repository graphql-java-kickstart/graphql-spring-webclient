package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
@AutoConfigureAfter({JacksonAutoConfiguration.class, OAuth2ClientAutoConfiguration.class, WebFluxAutoConfiguration.class})
@EnableConfigurationProperties(GraphQLClientProperties.class)
@ComponentScan(basePackageClasses = GraphQLWebClientImpl.class)
public class GraphQLWebClientAutoConfiguration {

  private final GraphQLClientProperties graphqlClientProperties;

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") WebClient.Builder clientBuilder,
      @Autowired(required = false) ReactiveClientRegistrationRepository clientRegistrations
  ) {
    clientBuilder.baseUrl(graphqlClientProperties.getUrl());

    if (clientRegistrations != null && clientRegistrations.findByRegistrationId("graphql").blockOptional().isPresent()) {
      ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
          new ServerOAuth2AuthorizedClientExchangeFilterFunction(
              clientRegistrations,
              new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
      oauth.setDefaultClientRegistrationId("graphql");
      clientBuilder.filter(oauth);
    }

    return clientBuilder.build();
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
