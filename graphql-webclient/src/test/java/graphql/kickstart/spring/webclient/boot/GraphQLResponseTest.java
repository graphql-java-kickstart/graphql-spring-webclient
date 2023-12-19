package graphql.kickstart.spring.webclient.boot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import com.jayway.jsonpath.PathNotFoundException;

import java.util.List;
import java.util.Map;

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
  void getErrors_noErrors_returnsEmptyList() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
    assertTrue(response.getErrors().isEmpty());
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
    assertNull(response.getFirst(String.class));
  }

  @Test
  void getFirstObject_notDataFieldExists_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": null } }");
    assertNull(response.getFirst(String.class));
  }

  @Test
  void getList_dataIsNull_returnsEmptyList() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
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

  @Test
  void getRawResponse() {
    GraphQLResponse response = constructResponse("{ \"data\": null }");
    assertEquals("{ \"data\": null }", response.getRawResponse());
  }
    
  void getAt_dataFieldExists_returnsValue() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": \"value\" } } }");
    String value = response.getAt("field.innerField", String.class);
    assertEquals("value", value);
  }

  @Test
  void getAt_noDataFieldExists_throwsException() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { } } }");
    GraphQLResponseReadException ex = assertThrows(GraphQLResponseReadException.class, () -> response.getAt("field.innerField", String.class));
    assertInstanceOf(PathNotFoundException.class, ex.getCause());
  }

  @Test
  void getAt_dataIsNull_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": null } } }");
    assertNull(response.getAt("field.innerField", String.class));
  }

  @Test
  void getListAt_dataFieldExists_returnsList() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": [\"value\"] } } }");
    List<String> values = response.getListAt("field.innerField", String.class);
    assertEquals(1, values.size());
    assertEquals("value", values.get(0));
  }

  @Test
  void getListAt_dataIsNull_returnsNull() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": null } } }");
    assertNull(response.getListAt("field.innerField", String.class));
  }

  @Test
  void getListAt_noDataFieldExists_throwsException() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { } } }");
    GraphQLResponseReadException ex = assertThrows(GraphQLResponseReadException.class, () -> response.getListAt("field.innerField", String.class));
    assertInstanceOf(PathNotFoundException.class, ex.getCause());
  }

  @Test
  void getAtAutoCast_dataFieldExists_returnsMap() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": { \"blah\": \"value\" } } } }");
    Map<String, String> value = response.getAt("field.innerField");
    assertEquals(1, value.size());
    assertEquals("value", value.get("blah"));
  }

  @Test
  void getAtAutoCast_dataFieldExists_returnsString() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": \"value\" } } }");
    String value = response.getAt("field.innerField");
    assertEquals("value", value);
  }

  @Test
  void getAtAutoCast_dataFieldExists_returnsInt() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": 42 } } }");
    Integer value = response.getAt("field.innerField");
    assertEquals(42, value);
  }

  @Test
  void getAtAutoCast_dataFieldExists_returnsDouble() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": 42.5 } } }");
    Double value = response.getAt("field.innerField");
    assertEquals(42.5, value);
  }

  @Test
  void getAtAutoCast_dataFieldExists_returnsList() {
    GraphQLResponse response = constructResponse("{ \"data\": { \"field\": { \"innerField\": [ \"value\", 42 ] } } }");
    List<Object> values = response.getAt("field.innerField");
    assertEquals(2, values.size());
    assertEquals("value", values.get(0));
    assertEquals(42, values.get(1));
  }

}
