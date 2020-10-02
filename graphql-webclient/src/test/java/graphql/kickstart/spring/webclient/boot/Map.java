package graphql.kickstart.spring.webclient.boot;

import java.util.HashMap;

class Map {

  static java.util.Map<String,Object> of(String key, String value) {
    java.util.Map<String,Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }

}
