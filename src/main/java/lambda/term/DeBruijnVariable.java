package lambda.term;

import java.util.Objects;

public class DeBruijnVariable implements Term {
  private final int index;

  public DeBruijnVariable(final int index) {
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

    final DeBruijnVariable deBruijnVariable = (DeBruijnVariable) o;
    return index == deBruijnVariable.index;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index);
  }

  @Override
  public String toString() {
    return "DeBruijnVariable{" + "index=" + index + '}';
  }
}
