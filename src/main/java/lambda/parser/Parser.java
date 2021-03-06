package lambda.parser;

import lambda.term.Term;

public interface Parser {
  Term parse(String expression);

  Term parseDeBruijn(String expression);

  String stringify(Term term);

  String stringifyDeBruijn(Term term);
}
