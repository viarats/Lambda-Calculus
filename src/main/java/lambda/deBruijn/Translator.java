package lambda.deBruijn;

import lambda.parser.LispParser;
import lambda.parser.Parser;

public class Translator {
  private static final Parser PARSER = new LispParser();

  public void addNames(final String term) {
    final var parsed = PARSER.parseDeBruijn(term);
  }

  public void removeNames(final String term) {
    final var parsed = PARSER.parse(term);
  }
}
