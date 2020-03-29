package graphql.kickstart.spring.webclient.boot;

import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Data
@Primary
@ConfigurationProperties(prefix = "graphql.client.oauth2")
class OAuth2ClientRegistrationProperties {

  private String provider;
  private String clientId;
  private String clientSecret;
  private String clientAuthenticationMethod;
  private String authorizationGrantType = "client_credentials";
  private String redirectUri;
  private Set<String> scope;
  private String clientName;

  private String authorizationUri;
  private String tokenUri;
  private String userInfoUri;
  private String userInfoAuthenticationMethod;
  private String userNameAttribute;
  private String jwkSetUri;
  private String issuerUri;

  Optional<ClientRegistration> getClientRegistration() {
    if (clientId != null) {
      ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("graphql")
          .clientId(getClientId())
          .clientSecret(getClientSecret())
          .redirectUriTemplate(getRedirectUri())
          .scope(getScope())
          .clientName(getClientName())
          .authorizationUri(getAuthorizationUri())
          .tokenUri(getTokenUri())
          .userInfoUri(getUserInfoUri())
          .jwkSetUri(getJwkSetUri());

      ofNullable(getClientAuthenticationMethod())
          .map(ClientAuthenticationMethod::new)
          .ifPresent(builder::clientAuthenticationMethod);

      ofNullable(getAuthorizationGrantType())
          .map(AuthorizationGrantType::new)
          .ifPresent(builder::authorizationGrantType);

      ofNullable(getUserInfoAuthenticationMethod())
          .map(AuthenticationMethod::new)
          .ifPresent(builder::userInfoAuthenticationMethod);

      return Optional.of(builder.build());
    }
    return Optional.empty();
  }

}
