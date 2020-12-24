package graphql.kickstart.spring.webclient.boot;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ThrowableNotThrown")
class GraphQLErrorsExceptionTest {

  @Test
  void construct_nullArg_throwsException() {
    //noinspection ConstantConditions
    assertThrows(NullPointerException.class, () -> new GraphQLErrorsException(null));
  }

  @Test
  void construct_emptyArg_throwsException() {
    List<GraphQLError> emptyList = emptyList();
    assertThrows(IllegalArgumentException.class, () -> new GraphQLErrorsException(emptyList));
  }

  @Test
  void construct_errors_returnsErrorsAndFirstMessage() {
    var error = new GraphQLError();
    error.setMessage("testmessage");
    var errors = List.of(error);
    GraphQLErrorsException e = new GraphQLErrorsException(errors);
    assertEquals(errors, e.getErrors());
    assertEquals("testmessage", e.getMessage());
    assertEquals("testmessage", e.getLocalizedMessage());
  }

}
