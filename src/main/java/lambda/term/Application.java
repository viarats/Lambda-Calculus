package lambda.term;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Application implements Term {
  private final Term function;
  private final Term argument;

  public Application(final Term function, final Term argument) {
    this.function = function;
    this.argument = argument;
  }

  public Term getFunction() {
    return function;
  }

  public Term getArgument() {
    return argument;
  }

  @Override
  public Set<Variable> getFreeVariables() {
    return Stream.concat(function.getFreeVariables().stream(), argument.getFreeVariables().stream())
        .collect(toUnmodifiableSet());
  }

  @Override
  public Set<Variable> getBoundVariables() {
    return Stream.concat(
            function.getBoundVariables().stream(), argument.getBoundVariables().stream())
        .collect(toUnmodifiableSet());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Application that = (Application) o;
    return Objects.equals(function, that.function) && Objects.equals(argument, that.argument);
  }

  @Override
  public int hashCode() {
    return Objects.hash(function, argument);
  }

  @Override
  public String toString() {
    return "Application{" + "function=" + function + ", argument=" + argument + '}';
  }
}
