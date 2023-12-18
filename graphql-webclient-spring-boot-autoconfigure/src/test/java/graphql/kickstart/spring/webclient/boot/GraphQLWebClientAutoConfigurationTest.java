package graphql.kickstart.spring.webclient.boot;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

class GraphQLWebClientAutoConfigurationTest {

  private GraphQLWebClientAutoConfiguration configuration;
  private GraphQLClientRetryProperties graphQLClientRetryProperties;
  private WebClient.Builder mockClientBuilder;

  @BeforeEach
  void setup() {
    GraphQLClientProperties graphQLClientProperties = new GraphQLClientProperties();
    graphQLClientRetryProperties = new GraphQLClientRetryProperties();
    configuration = new GraphQLWebClientAutoConfiguration(graphQLClientProperties, graphQLClientRetryProperties);

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
    assertNotNull(configuration.graphQLWebClient(WebClient.builder().build(), new ObjectMapper(), () -> null));
  }

  @Test
  void webClient_graphQLWebClientRetryErrorFilterPredicate_returns() {
    var predicate = configuration.graphQLWebClientRetryErrorFilterPredicate();
    assertNotNull(predicate);
    assertTrue(predicate.test(mock()));
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_noneStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.NONE);
    var provider = configuration.graphQLWebClientRetryProvider(mock());
    assertNotNull(provider);
    assertNull(provider.get());
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_backoffStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.BACKOFF);
    graphQLClientRetryProperties.getBackoff().setMaxAttempts(42);
    graphQLClientRetryProperties.getBackoff().setMinBackoff(Duration.ofMillis(50));
    graphQLClientRetryProperties.getBackoff().setMaxBackoff(Duration.ofMinutes(30));

    var mockedPredicate = mock(GraphQLWebClientRetryErrorFilterPredicate.class);
    when(mockedPredicate.test(isA(Throwable.class))).thenReturn(true);

    var provider = configuration.graphQLWebClientRetryProvider(mockedPredicate);
    assertNotNull(provider);
    assertNotNull(provider.get());
    assertInstanceOf(RetryBackoffSpec.class, provider.get());

    var castedProviderValue = (RetryBackoffSpec) provider.get();
    assertEquals(42, castedProviderValue.maxAttempts);
    assertEquals(Duration.ofMillis(50), castedProviderValue.minBackoff);
    assertEquals(Duration.ofMinutes(30), castedProviderValue.maxBackoff);

    castedProviderValue.errorFilter.test(new Throwable());
    verify(mockedPredicate, times(1)).test(isA(Throwable.class));
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_fixedDelayStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.FIXED_DELAY);
    graphQLClientRetryProperties.getFixedDelay().setMaxAttempts(42);
    graphQLClientRetryProperties.getFixedDelay().setDelay(Duration.ofMillis(50));

    var mockedPredicate = mock(GraphQLWebClientRetryErrorFilterPredicate.class);
    when(mockedPredicate.test(isA(Throwable.class))).thenReturn(true);

    var provider = configuration.graphQLWebClientRetryProvider(mockedPredicate);
    assertNotNull(provider);
    assertNotNull(provider.get());
    assertInstanceOf(RetryBackoffSpec.class, provider.get());

    var castedProviderValue = (RetryBackoffSpec) provider.get();
    assertEquals(42, castedProviderValue.maxAttempts);
    assertEquals(Duration.ofMillis(50), castedProviderValue.minBackoff);

    castedProviderValue.errorFilter.test(new Throwable());
    verify(mockedPredicate, times(1)).test(isA(Throwable.class));
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_indefinitelyStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.INDEFINITELY);

    var mockedPredicate = mock(GraphQLWebClientRetryErrorFilterPredicate.class);
    when(mockedPredicate.test(isA(Throwable.class))).thenReturn(true);

    var provider = configuration.graphQLWebClientRetryProvider(mockedPredicate);
    assertNotNull(provider);
    assertNotNull(provider.get());
    assertInstanceOf(RetrySpec.class, provider.get());

    var castedProviderValue = (RetrySpec) provider.get();

    castedProviderValue.errorFilter.test(new Throwable());
    verify(mockedPredicate, times(1)).test(isA(Throwable.class));
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_maxStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.MAX);
    graphQLClientRetryProperties.getMax().setMaxAttempts(42);

    var mockedPredicate = mock(GraphQLWebClientRetryErrorFilterPredicate.class);
    when(mockedPredicate.test(isA(Throwable.class))).thenReturn(true);

    var provider = configuration.graphQLWebClientRetryProvider(mockedPredicate);
    assertNotNull(provider);
    assertNotNull(provider.get());
    assertInstanceOf(RetrySpec.class, provider.get());

    var castedProviderValue = (RetrySpec) provider.get();
    assertEquals(42, castedProviderValue.maxAttempts);

    castedProviderValue.errorFilter.test(new Throwable());
    verify(mockedPredicate, times(1)).test(isA(Throwable.class));
  }

  @Test
  void webClient_graphQLWebClientRetryProvider_maxInRowStrategy() {
    graphQLClientRetryProperties.setStrategy(GraphQLClientRetryProperties.RetryStrategy.MAX_IN_ROW);
    graphQLClientRetryProperties.getMaxInRow().setMaxAttempts(42);

    var mockedPredicate = mock(GraphQLWebClientRetryErrorFilterPredicate.class);
    when(mockedPredicate.test(isA(Throwable.class))).thenReturn(true);

    var provider = configuration.graphQLWebClientRetryProvider(mockedPredicate);
    assertNotNull(provider);
    assertNotNull(provider.get());
    assertInstanceOf(RetrySpec.class, provider.get());

    var castedProviderValue = (RetrySpec) provider.get();
    assertEquals(42, castedProviderValue.maxAttempts);

    castedProviderValue.errorFilter.test(new Throwable());
    verify(mockedPredicate, times(1)).test(isA(Throwable.class));
  }

}
