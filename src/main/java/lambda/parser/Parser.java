package lambda.parser;

import lambda.term.Term;

public interface Parser {
  Term parse(String expression);

  lambda.deBruijn.term.Term parseDeBruijn(String expression);

  String stringify(Term term);

  String stringify(lambda.deBruijn.term.Term term);
}
