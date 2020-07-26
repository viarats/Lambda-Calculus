package lambda.deBruijn.term;

import java.util.Objects;

public class Variable implements Term {
  private final int index;

  public Variable(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
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
    return index == variable.index;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index);
  }
}
