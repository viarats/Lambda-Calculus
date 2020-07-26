package lambda.parser;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpList;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import de.tudresden.inf.lat.jsexp.SexpString;
import lambda.term.Abstraction;
import lambda.term.Application;
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
  public lambda.deBruijn.term.Term parseDeBruijn(final String expression) {
    try {
      final var sexp = SexpFactory.parse(expression);
      return parseDeBruijnSexp(sexp);
    } catch (final SexpParserException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String stringify(final Term term) {
    if (term instanceof Variable) {
      return ((Variable) term).getName();
    }

    if (term instanceof Abstraction) {
      final var abstraction = (Abstraction) term;
      return String.format(
          "(lambda %s %s)",
          stringify(abstraction.getParameter()), stringify(abstraction.getBody()));
    }

    if (term instanceof Application) {
      final var application = ((Application) term);
      return String.format(
          "(%s %s)", stringify(application.getFunction()), stringify(application.getArgument()));
    }

    throw new RuntimeException("Invalid term");
  }

  @Override
  public String stringify(final lambda.deBruijn.term.Term term) {
    if (term instanceof lambda.deBruijn.term.Variable) {
      return String.valueOf(((lambda.deBruijn.term.Variable) term).getIndex());
    }

    if (term instanceof lambda.deBruijn.term.Abstraction) {
      return String.format(
          "(lambda %s)", stringify(((lambda.deBruijn.term.Abstraction) term).getBody()));
    }

    if (term instanceof lambda.deBruijn.term.Application) {
      return String.format(
          "(%s %s)",
          stringify(((lambda.deBruijn.term.Application) term).getFunction()),
          stringify(((lambda.deBruijn.term.Application) term).getArgument()));
    }

    throw new RuntimeException("Invalid term");
  }

  private Term parseSexp(final Sexp sexp) {
    if (sexp instanceof SexpString) {
      return new Variable(sexp.toString());
    }

    if (sexp instanceof SexpList) {
      return parseSexpList((SexpList) sexp);
    }

    return null;
  }

  private Term parseSexpList(final SexpList sexpList) {
    switch (sexpList.getLength()) {
      case 2:
        return parseApplication(sexpList);
      case 3:
        return parseAbstraction(sexpList);
      default:
        return null;
    }
  }

  private Term parseApplication(final SexpList sexpList) {
    final var function = parseSexp(sexpList.get(0));
    final var argument = parseSexp(sexpList.get(1));

    return function == null || argument == null ? null : new Application(function, argument);
  }

  private Term parseAbstraction(final SexpList sexpList) {
    final var token = sexpList.get(0);
    if (token.getClass() != SexpString.class || !token.toString().equals(ABSTRACTION_TOKEN)) {
      return null;
    }

    final var parameter = ((Variable) parseSexp(sexpList.get(1)));
    final var body = parseSexp(sexpList.get(2));

    return parameter == null || body == null ? null : new Abstraction(parameter, body);
  }

  private lambda.deBruijn.term.Term parseDeBruijnSexp(final Sexp sexp) {
    if (sexp instanceof SexpString) {
      return new lambda.deBruijn.term.Variable(Integer.parseInt(sexp.toString()));
    }

    if (sexp instanceof SexpList) {
      return parseDeBruijnSexpList((SexpList) sexp);
    }

    return null;
  }

  private lambda.deBruijn.term.Term parseDeBruijnSexpList(final SexpList sexpList) {
    return sexpList.getLength() != 2
        ? null
        : sexpList.get(0).toString().equals(ABSTRACTION_TOKEN)
            ? parseDeBruijnAbstraction(sexpList)
            : parseDeBruijnApplication(sexpList);
  }

  private lambda.deBruijn.term.Term parseDeBruijnAbstraction(final SexpList sexpList) {
    return new lambda.deBruijn.term.Abstraction(parseDeBruijnSexp(sexpList.get(1)));
  }

  private lambda.deBruijn.term.Application parseDeBruijnApplication(final SexpList sexpList) {
    final var function = parseDeBruijnSexp(sexpList.get(0));
    final var argument = parseDeBruijnSexp(sexpList.get(1));

    return function == null || argument == null
        ? null
        : new lambda.deBruijn.term.Application(function, argument);
  }
}
