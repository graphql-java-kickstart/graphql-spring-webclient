package graphql.kickstart.spring.webclient.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("graphql.client")
class GraphQLClientProperties {

  private String url;

}
