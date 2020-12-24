package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class GraphQLWebClientAutoConfigurationTest {

  private GraphQLWebClientAutoConfiguration configuration;
  private WebClient.Builder mockClientBuilder;

  @BeforeEach
  void setup() {
    GraphQLClientProperties graphQLClientProperties = new GraphQLClientProperties();
    configuration = new GraphQLWebClientAutoConfiguration(graphQLClientProperties);

    mockClientBuilder = mock(WebClient.Builder.class);
  }

  @Test
  void webClient_nullClientRegistrations_doesNotAddFilter() {
    configuration.webClient(mockClientBuilder, null);
    verify(mockClientBuilder, times(0))
        .filter(isA(ServerOAuth2AuthorizedClientExchangeFilterFunction.class));
  }

  @Test
  void webClient_noGraphQLRegistration_doesNotAddFilter() {
    var registrationRepository = mock(ReactiveClientRegistrationRepository.class);
    when(registrationRepository.findByRegistrationId("graphql")).thenReturn(Mono.empty());
    configuration.webClient(mockClientBuilder, registrationRepository);
    verify(mockClientBuilder, times(0))
        .filter(isA(ServerOAuth2AuthorizedClientExchangeFilterFunction.class));
  }

  @Test
  void webClient_withGraphQLRegistration_doesAddFilter() {
    var registrationRepository = mock(ReactiveClientRegistrationRepository.class);
    var registration = ClientRegistration.withRegistrationId("graphql")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientId("clientId")
        .clientSecret("clientSecret")
        .tokenUri("tokenUri")
        .build();
    when(registrationRepository.findByRegistrationId("graphql")).thenReturn(Mono.just(registration));
    configuration.webClient(mockClientBuilder, registrationRepository);
    verify(mockClientBuilder, times(1))
        .filter(isA(ServerOAuth2AuthorizedClientExchangeFilterFunction.class));
  }

  @Test
  void webClient_getObjectMapper_returns() {
    assertNotNull(configuration.objectMapper());
  }

  @Test
  void webClient_graphQLWebClient_returns() {
    assertNotNull(configuration.graphQLWebClient(WebClient.builder().build(), new ObjectMapper()));
  }

}
