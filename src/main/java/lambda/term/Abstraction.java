package lambda.term;

import java.util.Objects;

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
