package graphql.kickstart.spring.webclient.boot;

import lombok.Value;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Value
class GraphQLRequestImpl implements GraphQLRequest {

  Map<String, Object> attributes;
  HttpHeaders headers;
  GraphQLRequestBody requestBody;

}
