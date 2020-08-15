package lambda.utils;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Set;
import java.util.stream.Stream;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.Term;
import lambda.term.Variable;

public class Util {

  public static Set<Variable> getFreeVariables(final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return Set.of((Variable) term);
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) term;
      return getFreeVariables(abstraction.getBody()).stream()
          .filter(not(var -> var.equals(abstraction.getParameter())))
          .collect(toUnmodifiableSet());
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return Stream.concat(
              getFreeVariables(application.getFunction()).stream(),
              getFreeVariables(application.getArgument()).stream())
          .collect(toUnmodifiableSet());
    }

    throw new RuntimeException(String.format("Unsupported term => %s", term));
  }

  public static Set<Variable> getBoundVariables(final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return Set.of();
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) term;
      return Stream.concat(
              Set.of(abstraction.getParameter()).stream(),
              getBoundVariables(abstraction.getBody()).stream())
          .collect(toUnmodifiableSet());
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return Stream.concat(
              getBoundVariables(application.getFunction()).stream(),
              getBoundVariables(application.getArgument()).stream())
          .collect(toUnmodifiableSet());
    }

    throw new RuntimeException(String.format("Unsupported term => %s", term));
  }
}
