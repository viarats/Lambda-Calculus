package lambda.term;

import java.util.Objects;
import java.util.Set;

public class Variable implements Term {
  private final String name;

  public Variable(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public Set<Variable> getFreeVariables() {
    return Set.of(this);
  }

  @Override
  public Set<Variable> getBoundVariables() {
    return Set.of();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Variable variable = (Variable) o;
    return Objects.equals(name, variable.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Variable{" + "name='" + name + '\'' + '}';
  }
}
