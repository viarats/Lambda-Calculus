package lambda.term;

import java.util.Objects;

public class DeBruijnAbstraction implements Term {
  private final lambda.term.Term body;

  public DeBruijnAbstraction(final Term body) {
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

    final DeBruijnAbstraction that = (DeBruijnAbstraction) o;
    return Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body);
  }

  @Override
  public String toString() {
    return "DeBruijnAbstraction{" + "body=" + body + '}';
  }
}
