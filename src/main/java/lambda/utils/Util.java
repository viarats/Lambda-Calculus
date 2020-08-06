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
      return getFreeVariables(((Abstraction) term).getBody()).stream()
          .filter(not(var -> var.equals(((Abstraction) term).getParameter())))
          .collect(toUnmodifiableSet());
    }

    if (type == Application.class) {
      return Stream.concat(
              getFreeVariables(((Application) term).getFunction()).stream(),
              getFreeVariables(((Application) term).getArgument()).stream())
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
      return Stream.concat(
              Set.of(((Abstraction) term).getParameter()).stream(),
              getBoundVariables(((Abstraction) term).getBody()).stream())
          .collect(toUnmodifiableSet());
    }

    if (type == Application.class) {
      return Stream.concat(
              getBoundVariables(((Application) term).getFunction()).stream(),
              getBoundVariables(((Application) term).getArgument()).stream())
          .collect(toUnmodifiableSet());
    }

    throw new RuntimeException(String.format("Unsupported term => %s", term));
  }
}
