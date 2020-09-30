package graphql.kickstart.spring.webclient.boot;

import lombok.Value;
import org.springframework.http.HttpHeaders;

@Value
class GraphQLRequestImpl<T> implements GraphQLRequest<T> {

  HttpHeaders headers;
  GraphQLRequestBody requestBody;
  Class<T> returnType;

}
