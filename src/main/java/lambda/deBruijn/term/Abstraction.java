package lambda.deBruijn.term;

import java.util.Objects;

public class Abstraction implements Term {
  private final Term body;

  public Abstraction(final Term body) {
    this.body = body;
  }

  public Term getBody() {
    return body;
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
    return Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body);
  }
}
