package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OAuth2ClientRegistrationRepositoryConfigurationTest {

  private OAuth2ClientRegistrationRepositoryConfiguration configuration;

  @BeforeEach
  void setup() {
    configuration = new OAuth2ClientRegistrationRepositoryConfiguration();
  }

  @Test
  void clientRegistrationRepository_nullRegistration_returnsNull() {
    var properties = new OAuth2ClientRegistrationProperties();
    var repository = configuration.reactiveClientRegistrationRepository(properties);
    assertNull(repository);
  }

  @Test
  void clientRegistrationRepository_withRegistration_returns() {
    var properties = new OAuth2ClientRegistrationProperties();
    properties.setClientId("client-id");
    properties.setClientSecret("client-secret");
    properties.setRedirectUri("redirect-uri");
    properties.setScope(Set.of("profile"));
    properties.setClientName("client-name");
    properties.setAuthorizationUri("authorization-uri");
    properties.setTokenUri("token-uri");
    properties.setUserInfoUri("user-info-uri");
    properties.setJwkSetUri("jwk-set-uri");
    properties.setProvider("provider");
    properties.setUserNameAttribute("username-attribute");
    properties.setIssuerUri("issuer-uri");
    var repository = configuration.reactiveClientRegistrationRepository(properties);
    assertNotNull(repository);
  }

}
