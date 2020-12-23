package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class GraphQLResponseTest {

  private final ObjectMapper mockedMapper = spy(new ObjectMapper());

  @Test
  void readRawResponse_throwsJsonProcessingException_isConvertedToCustomException() {
    GraphQLClientException e = assertThrows(GraphQLClientException.class,
        () -> constructResponse("no valid json"));
    assertEquals("Cannot read response 'no valid json'", e.getMessage());
  }

  private GraphQLResponse constructResponse(String json) {
    return new GraphQLResponse(json, mockedMapper);
  }

  @Test
  void getFieldName_dataIsNull_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
    assertNull(response.get("test", String.class));
  }

  @Test
  void getFieldName_notDataFieldExists_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": null } }");
    assertNull(response.get("test", String.class));
  }

  @Test
  void getFieldName_dataFieldIsNull_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": null } }");
    assertNull(response.get("field", String.class));
    verify(mockedMapper, times(0)).convertValue(null, String.class);
  }

  @Test
  void getFieldName_dataFieldExists_returnsValue() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": \"value\" } }");
    String value = response.get("field", String.class);
    assertEquals("value", value);
    verify(mockedMapper, times(1)).convertValue(isA(JsonNode.class), eq(String.class));
  }

  @Test
  void getFirstObject_dataIsNull_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
    assertNull(response.getFirstObject(String.class));
  }

  @Test
  void getFirstObject_notDataFieldExists_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": null } }");
    assertNull(response.getFirstObject(String.class));
  }

  @Test
  void getList_dataIsNull_returnsEmptyList() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
    assertNull(response.getData());
    assertTrue(response.getList("test", String.class).isEmpty());
  }

  @Test
  void getList_notDataFieldExists_returnsEmptyList() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": null } }");
    assertTrue(response.getList("test", String.class).isEmpty());
  }

  @Test
  void getList_dataFieldExists_returnsList() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": [\"value\"] } }");
    List<String> values = response.getList("field", String.class);
    assertEquals(1, values.size());
    assertEquals("value", values.get(0));
  }

}
