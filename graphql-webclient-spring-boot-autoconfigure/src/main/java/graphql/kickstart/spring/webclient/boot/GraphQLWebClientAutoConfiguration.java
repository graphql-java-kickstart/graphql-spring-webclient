package graphql.kickstart.spring.webclient.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@AutoConfigureAfter({
  JacksonAutoConfiguration.class,
  OAuth2ClientAutoConfiguration.class,
  WebFluxAutoConfiguration.class
})
@EnableConfigurationProperties(GraphQLClientProperties.class)
@ComponentScan(basePackageClasses = GraphQLWebClientImpl.class)
public class GraphQLWebClientAutoConfiguration {

  private final GraphQLClientProperties graphqlClientProperties;

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient(
      WebClient.Builder clientBuilder,
      @Autowired(required = false) ReactiveClientRegistrationRepository clientRegistrations) {
    clientBuilder.baseUrl(graphqlClientProperties.getUrl());

    if (isGraphQLClientRegistrationPresent(clientRegistrations)) {
      clientBuilder.filter(createOAuthFilter(clientRegistrations));
    }

    return clientBuilder.build();
  }

  @NonNull
  private ServerOAuth2AuthorizedClientExchangeFilterFunction createOAuthFilter(
      ReactiveClientRegistrationRepository clientRegistrations) {
    InMemoryReactiveOAuth2AuthorizedClientService clientService =
        new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrations, clientService);
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    oauth.setDefaultClientRegistrationId("graphql");
    return oauth;
  }

  private boolean isGraphQLClientRegistrationPresent(
      ReactiveClientRegistrationRepository clientRegistrations) {
    return clientRegistrations != null
        && clientRegistrations.findByRegistrationId("graphql").blockOptional().isPresent();
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
