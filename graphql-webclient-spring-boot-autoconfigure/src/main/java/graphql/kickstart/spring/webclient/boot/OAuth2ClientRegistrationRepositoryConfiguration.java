package graphql.kickstart.spring.webclient.boot;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@Configuration
@EnableConfigurationProperties(OAuth2ClientRegistrationProperties.class)
class OAuth2ClientRegistrationRepositoryConfiguration {

  @Bean
  @ConditionalOnMissingBean({ReactiveClientRegistrationRepository.class})
  InMemoryReactiveClientRegistrationRepository clientRegistrationRepository(OAuth2ClientRegistrationProperties properties) {
    return properties.getClientRegistration()
        .map(Arrays::asList)
        .map(InMemoryReactiveClientRegistrationRepository::new)
        .orElse(null);
  }

}
