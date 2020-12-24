package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphQLRequestBuilderTest {

  private GraphQLRequestBuilder builder;

  @BeforeEach
  void setup() {
    builder = GraphQLRequest.builder();
  }

  @Test
  void operationName_addsToRequest() {
    GraphQLRequest request = builder.operationName("some-operation").build();
    assertEquals("some-operation", request.getRequestBody().getOperationName());
  }

}
