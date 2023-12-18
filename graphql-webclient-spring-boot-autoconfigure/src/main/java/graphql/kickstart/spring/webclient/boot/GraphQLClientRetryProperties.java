package graphql.kickstart.spring.webclient.boot;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

import lombok.Data;

@Data
@Primary
@ConfigurationProperties("graphql.client.retry")
public class GraphQLClientRetryProperties {

  private RetryStrategy strategy = RetryStrategy.NONE;
  private RetryBackoff backoff = new RetryBackoff();
  private RetryFixedDelay fixedDelay = new RetryFixedDelay();
  private RetryMax max = new RetryMax();
  private RetryMaxInRow maxInRow = new RetryMaxInRow();

  @Data
  static class RetryBackoff {
    private long maxAttempts = -1;
    private Duration minBackoff = Duration.ofMillis(0);
    private Duration maxBackoff = Duration.ofMillis(Long.MAX_VALUE);
  }

  @Data
  static class RetryFixedDelay {
    private long maxAttempts = -1;
    private Duration delay = Duration.ofMillis(0);
  }

  @Data
  static class RetryMax {
    private long maxAttempts = -1;
  }

  @Data
  static class RetryMaxInRow {
    private long maxAttempts = -1;
  }

  enum RetryStrategy {
    BACKOFF,
    FIXED_DELAY,
    INDEFINITELY,
    MAX,
    MAX_IN_ROW,
    NONE
  }
}
