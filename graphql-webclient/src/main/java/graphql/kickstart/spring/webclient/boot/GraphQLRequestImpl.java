package graphql.kickstart.spring.webclient.boot;

import lombok.Value;
import org.springframework.http.HttpHeaders;

@Value
class GraphQLRequestImpl implements GraphQLRequest {

  HttpHeaders headers;
  GraphQLRequestBody requestBody;

}
