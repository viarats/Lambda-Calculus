package lambda.parser;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpList;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import de.tudresden.inf.lat.jsexp.SexpString;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.DeBruijnAbstraction;
import lambda.term.DeBruijnVariable;
import lambda.term.Term;
import lambda.term.Variable;

public class LispParser implements Parser {
  private static final String ABSTRACTION_TOKEN = "lambda";

  @Override
  public Term parse(final String expression) {
    try {
      final var sexp = SexpFactory.parse(expression);
      return parseSexp(sexp);
    } catch (final SexpParserException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Term parseDeBruijn(final String expression) {
    try {
      final var sexp = SexpFactory.parse(expression);
      return parseDeBruijnSexp(sexp);
    } catch (final SexpParserException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String stringify(final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return ((Variable) term).getName();
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) term;
      return String.format(
          "(lambda %s %s)",
          stringify(abstraction.getParameter()), stringify(abstraction.getBody()));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return String.format(
          "(%s %s)", stringify(application.getFunction()), stringify(application.getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  @Override
  public String stringifyDeBruijn(final Term term) {
    final var type = term.getClass();
    if (type == DeBruijnVariable.class) {
      return String.valueOf(((DeBruijnVariable) term).getIndex());
    }

    if (type == DeBruijnAbstraction.class) {
      return String.format(
          "(lambda %s)", stringifyDeBruijn(((DeBruijnAbstraction) term).getBody()));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return String.format(
          "(%s %s)",
          stringifyDeBruijn(application.getFunction()),
          stringifyDeBruijn(application.getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private Term parseSexp(final Sexp sexp) {
    final var type = sexp.getClass();
    if (type == SexpString.class) {
      return new Variable(sexp.toString());
    }

    if (type == SexpList.class) {
      return parseSexpList((SexpList) sexp);
    }

    throw new RuntimeException("Invalid expression");
  }

  private Term parseSexpList(final SexpList sexpList) {
    switch (sexpList.getLength()) {
      case 2:
        return parseApplication(sexpList);
      case 3:
        return parseAbstraction(sexpList);
      default:
        throw new RuntimeException("Invalid expression");
    }
  }

  private Term parseApplication(final SexpList sexpList) {
    final var function = parseSexp(sexpList.get(0));
    final var argument = parseSexp(sexpList.get(1));

    if (function != null && argument != null) {
      return new Application(function, argument);
    }

    throw new RuntimeException("Invalid expression");
  }

  private Term parseAbstraction(final SexpList sexpList) {
    final var token = sexpList.get(0);
    if (token.getClass() == SexpString.class && token.toString().equals(ABSTRACTION_TOKEN)) {
      final var parameter = ((Variable) parseSexp(sexpList.get(1)));
      final var body = parseSexp(sexpList.get(2));

      if (parameter != null && body != null) {
        return new Abstraction(parameter, body);
      }
    }

    throw new RuntimeException("Invalid expression");
  }

  private Term parseDeBruijnSexp(final Sexp sexp) {
    final var type = sexp.getClass();
    if (type == SexpString.class) {
      return new DeBruijnVariable(Integer.parseInt(sexp.toString()));
    }

    if (type == SexpList.class) {
      return parseDeBruijnSexpList((SexpList) sexp);
    }

    throw new RuntimeException("Invalid expression");
  }

  private Term parseDeBruijnSexpList(final SexpList sexpList) {
    if (sexpList.getLength() >= 2) {
      return sexpList.get(0).toString().equals(ABSTRACTION_TOKEN)
          ? parseDeBruijnAbstraction(sexpList)
          : parseDeBruijnApplication(sexpList);
    }

    throw new RuntimeException("Invalid expression");
  }

  private Term parseDeBruijnAbstraction(final SexpList sexpList) {
    return new DeBruijnAbstraction(parseDeBruijnSexp(sexpList.get(1)));
  }

  private Application parseDeBruijnApplication(final SexpList sexpList) {
    final var function = parseDeBruijnSexp(sexpList.get(0));
    final var argument = parseDeBruijnSexp(sexpList.get(1));

    if (function != null && argument != null) {
      return new Application(function, argument);
    }

    throw new RuntimeException("Invalid expression");
  }
}
