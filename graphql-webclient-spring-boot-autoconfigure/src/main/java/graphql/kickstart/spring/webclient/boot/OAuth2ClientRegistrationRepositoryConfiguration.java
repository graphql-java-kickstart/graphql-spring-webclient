package graphql.kickstart.spring.webclient.boot;

import static java.util.Collections.singletonList;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;

@Configuration
@EnableConfigurationProperties(OAuth2ClientRegistrationProperties.class)
class OAuth2ClientRegistrationRepositoryConfiguration {

  @Bean
  @ConditionalOnMissingBean({ClientRegistrationRepository.class})
  InMemoryReactiveClientRegistrationRepository clientRegistrationRepository(OAuth2ClientRegistrationProperties properties) {
    List<ClientRegistration> registrations = singletonList(properties.getClientRegistration());
    return new InMemoryReactiveClientRegistrationRepository(registrations);
  }

}
