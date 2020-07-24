package lambda.parser;

import lambda.term.Term;

public interface Parser {
  Term parse(String expression);

  String stringify(Term term);
}
