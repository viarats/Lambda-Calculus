package lambda.term;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Abstraction implements Term {
  private final Variable parameter;
  private final Term body;

  public Abstraction(final Variable parameter, final Term body) {
    this.parameter = parameter;
    this.body = body;
  }

  public Variable getParameter() {
    return parameter;
  }

  public Term getBody() {
    return body;
  }

  @Override
  public Set<Variable> getFreeVariables() {
    return body.getFreeVariables().stream()
        .filter(var -> var.equals(parameter))
        .collect(toUnmodifiableSet());
  }

  @Override
  public Set<Variable> getBoundVariables() {
    return Stream.concat(Set.of(parameter).stream(), body.getBoundVariables().stream())
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

    final Abstraction that = (Abstraction) o;
    return Objects.equals(parameter, that.parameter) && Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameter, body);
  }

  @Override
  public String toString() {
    return "Abstraction{" + "parameter=" + parameter + ", body=" + body + '}';
  }
}
