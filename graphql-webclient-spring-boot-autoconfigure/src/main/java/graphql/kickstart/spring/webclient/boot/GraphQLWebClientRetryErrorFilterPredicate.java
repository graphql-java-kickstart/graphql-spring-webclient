package graphql.kickstart.spring.webclient.boot;

import java.util.function.Predicate;

public interface GraphQLWebClientRetryErrorFilterPredicate extends Predicate<Throwable> {
}
