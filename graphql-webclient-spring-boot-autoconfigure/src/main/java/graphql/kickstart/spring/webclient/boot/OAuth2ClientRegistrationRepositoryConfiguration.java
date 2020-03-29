package graphql.kickstart.spring.webclient.boot;

import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@Configuration
@EnableConfigurationProperties(OAuth2ClientRegistrationProperties.class)
class OAuth2ClientRegistrationRepositoryConfiguration {

  @Bean
  @ConditionalOnMissingBean({ReactiveClientRegistrationRepository.class})
  InMemoryReactiveClientRegistrationRepository clientRegistrationRepository(OAuth2ClientRegistrationProperties properties) {
    List<ClientRegistration> registrations = properties.getClientRegistration()
        .map(List::of)
        .orElseGet(Collections::emptyList);
    return new InMemoryReactiveClientRegistrationRepository(registrations);
  }

}
